# Geração Livre

Loja virtual de **fitas de gorgurão e cetim vendidas por metro**, com React, Java 21/Spring Boot e PostgreSQL. A primeira entrega inclui catálogo, detalhe, cadastro/login, carrinho visitante e por conta, mesclagem e administração básica de produtos.

O planejamento e as pendências estão em [PLANO_IMPLEMENTACAO.md](PLANO_IMPLEMENTACAO.md).

## Executar a demonstração local

Pré-requisitos: Java 21, Maven e Node.js 22 com npm. Não é necessário Docker para a demonstração.

No primeiro terminal:

```powershell
cd Back-End
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

No segundo terminal:

```powershell
cd Front-End
npm ci
npm start
```

Acesse **http://localhost:3000**. A API usa a porta 8080; o frontend encaminha `/api` para ela. O perfil `dev` usa H2 em memória: contas, produtos e carrinhos criados na demonstração desaparecem ao encerrar o processo Java. O carrinho visitante permanece no navegador.

Conta administrativa exclusiva da demonstração:

- E-mail: `admin@demo.local`
- Senha: `DemoAdmin123!`

Depois de entrar, use **Administrar** para cadastrar, editar e retirar fitas. Cada largura/cor/estampa recebe SKU próprio. Preço e estoque são por metro. Corte mínimo e incremento são obrigatórios e configuráveis; 0,50 m/0,10 m são apenas exemplos dos produtos demonstrativos. O catálogo exibe ilustrações enquanto não há fotos. Nesta entrega, imagens podem ser associadas por URL; upload virá em outro lote.

## PostgreSQL e Docker

1. Inicie o Docker Desktop.
2. Copie `.env.example` para `.env` somente se ainda não houver configuração local; preserve os valores existentes quando houver.
3. Preencha `POSTGRES_PASSWORD` e `JWT_SECRET` (segredo aleatório com pelo menos 32 bytes).
4. Para criar o primeiro administrador, defina `BOOTSTRAP_ADMIN_ENABLED=true`, `BOOTSTRAP_ADMIN_EMAIL` e `BOOTSTRAP_ADMIN_PASSWORD` (pelo menos 12 caracteres).
5. Execute na raiz:

```powershell
docker compose up --build
```

A loja estará na porta 3000. Os dados ficam no volume PostgreSQL. Neste modo não são criados produtos ou credenciais de demonstração; as categorias Lisas e Estampadas são provisionadas por migração. Cadastre as fitas pelo painel. Após criar o administrador inicial, desabilite o bootstrap e remova sua senha das variáveis de provisionamento. O bootstrap não promove uma conta de cliente existente e não substitui senha de administrador já cadastrado.

Também é possível executar o backend diretamente com PostgreSQL configurando `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` e `JWT_SECRET` no ambiente. O Maven não carrega `.env` automaticamente. O perfil padrão valida o schema e exige o segredo JWT; use `dev` somente para demonstração.

### Banco já existente

Flyway não faz baseline automático. Antes de conectar um banco antigo, faça backup e compare sua estrutura com `V1__estrutura_original.sql`. Somente após essa verificação registre o baseline na versão 1 pelo procedimento do Flyway. A migração V2 preserva registros e converte quantidades para decimal, mas não supõe que produtos antigos sejam gorgurão: ficam fora do catálogo público até revisão. Não habilite baseline automático para ignorar diferenças de schema.

## Verificar a implementação

```powershell
cd Back-End
mvn test
mvn package
```

```powershell
cd Front-End
npm test -- --watchAll=false --runInBand
npm run build
```

Validação desta entrega: 11 testes de backend aprovados em H2 e em um PostgreSQL 18 temporário, 4 testes de frontend aprovados, build de produção compilado e jornada validada em navegador desktop/móvel, incluindo atualização de preço em outra aba. A configuração Compose foi validada; os containers não foram executados porque o Docker Desktop estava desligado.

Os testes de integração usam o perfil `dev` e um banco descartável. Cobrem metragem, estoque, autorização, catálogo, mesclagem idempotente/atômica e bloqueio do checkout não integrado. Não aponte a suíte para um banco com dados reais.

## Contratos desta entrega

Quantidades são decimais em metros com até duas casas; preços são decimais em reais com até duas casas. A regra válida é `quantidade >= quantidadeMinima` e `(quantidade - quantidadeMinima)` múltiplo de `incremento`, respeitando estoque. Exemplo ilustrativo: mínimo 0,50 e incremento 0,10 permitem 0,50, 0,60, 0,70 etc. Subtotais são arredondados para centavos.

| Operação | Rota |
| --- | --- |
| Configuração pública | `GET /api/produtos/configuracao` |
| Categorias | `GET /api/categorias` |
| Catálogo paginado | `GET /api/produtos/paginado?pagina=0&tamanho=12&busca=rosa&largura=38&categoriaId=1` |
| Detalhe público | `GET /api/produtos/{id}` |
| Atualizações do catálogo | `GET /api/produtos/eventos` (SSE, evento `catalogo`) |
| Cadastro/login | `POST /api/auth/register`, `POST /api/auth/login` |
| Perfil atual | `GET /api/auth/me` |
| Carrinho da conta | `GET /api/carrinho` |
| Adição | `POST /api/carrinho/adicionar` com `produtoId` e `quantidade` |
| Alteração/remoção | `PUT /api/carrinho/item/{id}`, `DELETE /api/carrinho/item/{id}` |
| Mesclagem | `POST /api/carrinho/mesclar` com `chave` e `itens` |
| Administração | `GET/POST /api/admin/produtos`, `PUT/DELETE /api/admin/produtos/{id}` |

Rotas privadas exigem `Authorization: Bearer <token>`. Administração exige `ROLE_ADMIN`; cadastro público sempre cria cliente. O logout remove a sessão do navegador e mantém o carrinho no banco. Revogação de tokens no servidor ainda está pendente.

Paginação retorna `itens`, `pagina`, `totalPaginas` e `total`. Produto possui `material=GORGURAO`, `sku`, `larguraMm`, `cor`, `estampa`, `preco`, `precoPromocional`, `estoque`, `quantidadeMinima`, `incremento`, `categoriaId`, `imagemUrl` e `ativo`. A API aceita `GORGURAO` e `CETIM` e rejeita outros materiais. O carrinho inclui dados atualizados do produto e total calculado no servidor.

Mesclagem reúne o carrinho visitante ao da conta sob transação. A chave deve permanecer igual durante tentativas de repetir a mesma mesclagem. Uma chave já processada não adiciona novamente. Em conflito de estoque/regra/indisponibilidade, nenhuma parte é gravada e o navegador preserva os itens para revisão; após corrigir a seleção, entre novamente. Se o cadastro já foi criado, use Entrar, não repita o cadastro.

Erros de domínio retornam `message`; erros de formulário incluem `campos`. Códigos: 400 para entrada inválida, 401 para autenticação, 403 para acesso indevido, 404 para detalhe inexistente/inativo, 409 para conflito e 503 para checkout ainda não configurado.

## Próximas entregas

Recuperação de senha, bloqueio de conta, endereços, preferências por cliente, upload de imagens, favoritos, atendimento, integração de frete/pagamento e operação de pedidos. O checkout está indisponível por padrão na interface e na API. Esta versão não realiza cobrança real nem está pronta para operação comercial.


## Fitas de cetim cadastradas - 12/10/2026

Cinco fitas de cetim de 38 mm, com fotos originais fornecidas pelo responsável: Azul Oceano (CF009 2105), Rosa Magnólia (CF009 2101), Bordô (CF009 1377), Laranja Abóbora (CF009 1235) e Verde Cítrico (CF009 280).

Valores temporários autorizados: R$ 4,90 por metro e 30 metros de estoque por cor. Regras definidas pelo responsável: corte mínimo de 1 metro e incremento de 0,5 metro. Os produtos são cadastrados automaticamente no perfil `dev`, sem duplicar SKUs nem sobrescrever edições feitas no painel. Os valores iniciais ficam em `Back-End/src/main/resources/catalogo/fitas-cetim.json`. As fotos ficam em `Front-End/public/products/cetim`.

Em outros ambientes, o painel oferece os cinco cadastros preenchidos para revisão e salvamento. O cadastro automático com valores de teste fica restrito à demonstração.
