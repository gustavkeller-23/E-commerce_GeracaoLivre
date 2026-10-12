# Plano de implementação - Geração Livre

Data: 08/10/2026.

## 1. Objetivo e base do planejamento

Implementar uma loja virtual com catálogo de fitas de gorgurão e cetim, permitindo que a Geração Livre venda para clientes de outras regiões e que a proprietária gerencie catálogo, estoque e pedidos. Tecidos e outros artigos para costura e artesanato ficam fora do escopo de venda inicial.

Fonte de requisitos: `../Trabalho_Engenharia_de_Software_2.pdf`, 27 páginas. Foram lidas as oito histórias de usuário (US01 a US08), os nove casos de uso (UC01 a UC09), a modelagem e a arquitetura. O PDF escreve os identificadores UC7 e UC8 sem zero; este plano os normaliza para UC07 e UC08.

Atualização de escopo confirmada pelo responsável: manter as tecnologias do repositório e a arquitetura da seção 5, vender inicialmente apenas fitas de gorgurão e adotar as oito decisões de fluxo da seção 3. Essas definições prevalecem sobre as divergências do PDF.

Este plano cruza os requisitos com o código existente. A inspeção é estática: a aplicação não foi compilada nem executada nesta etapa; presença de código não comprova funcionamento. A pasta Git estava sem alterações antes da criação deste documento.

## 2. Diagnóstico inicial do projeto (antes da implementação)

| Área | Evidência no código | Situação e trabalho necessário |
| --- | --- | --- |
| Interface | `Front-End/src/App.js` contém a página padrão do React | Construir todas as telas da loja e da administração e integrar com a API. |
| Tecnologias | React no frontend; Java 21, Spring Boot 3.3.4, JPA e PostgreSQL no backend | Reaproveitar a estrutura, validar instalação, compilação e compatibilidade antes de implementar funcionalidades. |
| Autenticação | Cadastro/login, BCrypt, filtro JWT e perfis cliente/admin | Faltam recuperação de senha, política de sessão/logout, conta bloqueada e telas. Telefone não é obrigatório no DTO, embora seja solicitado na US01. |
| Catálogo | Listagem, busca, categoria, destaques e detalhe | Falta paginação. O detalhe busca por ID sem aplicar o filtro de produto ativo. |
| Fitas de gorgurão | Estoque e quantidades como `Integer`; produto genérico | Adequar o catálogo ao escopo exclusivo de gorgurão. Faltam largura, cor/estampa, variantes e definição de venda por metro, corte ou rolo. Metragem fracionada depende da unidade comercial escolhida. |
| Carrinho | Operações de adicionar, atualizar, remover e limpar com vínculo ao usuário | Falta carrinho visitante e sua mesclagem no login. Revisar produto inativo, preço alterado e concorrência. |
| Favoritos | Lista e operação de alternar favorito | Falta interface; definir comportamento para itens retirados do catálogo e operações repetidas. |
| Checkout | Pedido, endereço copiado, total, promoção, frete fixo e cupons codificados | Não há consulta de CEP, seleção de endereço salvo ou cotação real. Revisar regras comerciais e validação. |
| Pagamento | Pix/boleto construídos por concatenação; cartão confirmado automaticamente | É uma simulação. Implementar integração de pagamento e confirmação confiável antes de operar vendas reais. |
| Pedidos | Listagem do cliente e consulta com checagem do proprietário | Faltam telas, histórico de mudanças, rastreio e controle das transições. |
| Administração | Serviços de produto, pedido e mensagem; proteção prevista para `/api/admin/**` | Não existem controllers administrativos na árvore inspecionada. Faltam painel e upload de imagens. |
| Atendimento | Mensagem salva no banco; serviço permite registrar resposta | Falta envio ao e-mail da loja, WhatsApp e acesso administrativo. |
| Infraestrutura | Compose com PostgreSQL, backend e frontend | O Compose referencia um Dockerfile do frontend que não existe. Backend empacota com testes ignorados; não foram encontrados testes de backend. |
| Configuração | Schema com `ddl-auto=update`, CORS abrangente e segredo JWT com valor padrão | Separar ambientes, versionar schema, restringir origens e exigir configuração segura em produção. Não copiar valores reais de `.env` para documentos ou logs. |

Arquivos principais: `Back-End/src/main/java/com/geracaolivre/ecommerce/{controller,service,model,repository,security,dto}`, `Front-End/src` e `docker-compose.yml`.

## 3. Decisões confirmadas e regras comerciais pendentes

### Continuidade tecnológica

Decisão confirmada pelo responsável: continuar com React/Spring/PostgreSQL e com a arquitetura de implementação descrita na seção 5. Não é necessário seguir Flutter/Dart/Firebase propostos no PDF (páginas 22 a 24 e diagrama na 27). Preservar os requisitos funcionais aplicáveis ao catálogo exclusivo de fitas de gorgurão e atualizar a documentação arquitetural para refletir as tecnologias do repositório. A escolha tecnológica não precisa de nova confirmação.

Manter aplicação em camadas e backend único, organizado por funcionalidades. Não há requisito que justifique dividir o sistema em vários serviços. Escolher versões e ferramentas complementares na etapa 0 após validar o ambiente, sem trocar tecnologias apenas para começar o projeto.

### Escopo e regras específicas de fitas de gorgurão

O escopo exclusivo de fitas de gorgurão e a venda por metro estão confirmados pelo responsável. Corte mínimo e incremento permanecem a definir; serão configuráveis por produto.

- Restringir cadastro, catálogo, busca, filtros e dados demonstrativos a fitas de gorgurão. Não oferecer tecidos, outras fitas ou outros artigos nesta versão.
- Vender inicialmente apenas por metro, com preço por metro e metragem explícitos na tela. Cortes fixos e rolos fechados não integram a apresentação comercial inicial.
- Usar quantidade decimal de ponta a ponta e definir mínimo e incremento. Exemplo ilustrativo a validar: mínimo de 0,50 m e incrementos de 0,10 m. Não usar ponto flutuante para cálculos financeiros.
- Mostrar largura em milímetros, cor/estampa, composição quando informada pelo fornecedor e imagens. Para cortes e rolos, mostrar também comprimento em metros.
- Definir se combinações de largura, cor/estampa e apresentação serão produtos separados ou variantes com SKU e estoque próprio. A escolha do cliente deve identificar exatamente a fita comprada.
- Validar se é necessário controlar rolos para cortes contínuos: soma de metragem não garante um corte contínuo. Se necessário, reservar no rolo escolhido.
- Para cortes fixos e rolos fechados vendidos por unidade, rejeitar quantidade fracionada e manter o comprimento associado ao SKU.

### Divergências e escolhas de fluxo

As oito diretrizes abaixo foram confirmadas pelo responsável e orientam a implementação. Permanecem pendentes apenas os parâmetros comerciais explicitamente indicados.

| Questão | Fonte | Decisão confirmada |
| --- | --- | --- |
| Visitante pode usar carrinho? | US05 permite armazenamento no navegador; UC06 declara usuário logado | Permitir carrinho visitante e exigir autenticação na finalização. Mesclar com o carrinho da conta sem perder itens nem ultrapassar estoque. |
| Carrinho sobrevive ao logout? | US05 prevê persistência na conta; UC06 menciona estado até logout | Manter carrinho no banco, limpar apenas dados da sessão no navegador. Não expor o carrinho de uma conta à próxima. |
| Meios de pagamento | US06 exige Pix, cartão e boleto; UC07 descreve depósito | Implementar apenas Pix, cartão de crédito e boleto. Depósito não integra o escopo e não é uma decisão pendente. |
| Confirmação do pedido ou do pagamento? | US06 e UC07 combinam confirmação, número de pedido e status pendente | Criar pedido pendente com número único; enviar confirmação de recebimento e atualizar pagamento somente pelo retorno validado do provedor. |
| Resposta às mensagens no painel | US07 adia para versões futuras; UC09 inclui visualização de mensagens | Entregar visualização no painel e resposta por e-mail na primeira versão; resposta dentro do painel fica para evolução. |
| Preferências e atualização em tempo real | UC03 pede preferências; UC04 pede atualização em tempo real | Persistir filtros do cliente e implementar atualização automática de preço/estoque. Reconsulta ao focar a página pode complementar, mas não satisfaz sozinha a exigência estrita de tempo real. |
| Conta bloqueada | Fluxo alternativo UC02 | Modelar estado da conta, rejeitar autenticação e acesso conforme política definida. |
| Frete e cupons | Código contém valores e códigos fixos sem definição equivalente no PDF | Tratar como exemplos existentes; validar regras antes de usá-las comercialmente. |

Antes da integração comercial, definir com a proprietária: corte mínimo, incremento e necessidade de controle por rolo das fitas de gorgurão, frete e área atendida, cupons, provedor de pagamento para Pix/cartão de crédito/boleto, prazo de reserva, política de bloqueio de conta, canais de contato, hospedagem e imagens do catálogo. As oito diretrizes e a arquitetura já estão aprovadas. As escolhas comerciais restantes não impedem preparar o ambiente, contratos e protótipos.

## 4. Escopo e rastreabilidade

Preservar a classificação MoSCoW do PDF. O MVP cobre os requisitos Must Have; a versão completa inclui também os Should Have. Recursos dos casos de uso que ampliam as histórias ficam explicitados para evitar perda de escopo.

Todas as histórias e casos de uso abaixo se aplicam exclusivamente à venda de fitas de gorgurão nesta versão. Busca, categorias e filtros devem organizar esse catálogo por características das fitas, sem ampliar o escopo para outros produtos.

| História | Casos de uso / páginas do PDF | Prioridade | Entrega prevista | Principais critérios de aceite |
| --- | --- | --- | --- | --- |
| US01 - Cadastro/Login | UC01 e UC02; pp. 3-4, 9-10 | Must Have | Etapa 2 | Cadastro com validações, e-mail único, login automático após cadastro, nome na interface, erro de credenciais, recuperação por e-mail, logout e conta bloqueada. |
| US02 - Listagem | UC03; pp. 4, 11 | Must Have | Etapa 3 | Grade pública com imagem/nome/preço, busca, categorias, paginação, lista vazia, falha de filtros e preferências do cliente. |
| US03 - Produto | UC04; pp. 4-5, 12 | Must Have | Etapa 3; favorito na 6 | Detalhe, imagem ampliada, quantidade, estoque, compra bloqueada quando indisponível, retorno à listagem e atualização automática. |
| US04 - Favoritos | UC05; pp. 5, 13 | Should Have | Etapa 6 | Exigir login, adicionar/remover com confirmação, persistir por conta, lista vazia e preservar estado em falha. |
| US05 - Carrinho | UC06; pp. 5-6, 14 | Must Have | Etapa 4 | Adição pelo catálogo/detalhe, edição, remoção, subtotais e total, persistência, visitante, estoque e estado vazio. |
| US06 - Finalizar compra | UC07; pp. 6, 15-16 | Must Have | Etapa 5 | Resumo, endereço informado/salvo, CEP, frete, promoções/cupons, Pix/cartão/boleto, pedido associado à conta, confirmação por e-mail e tentativa após falha. |
| US07 - Atendimento | UC08; pp. 6-7, 17 | Should Have | Etapa 6 | Formulário validado, persistência, encaminhamento por e-mail, WhatsApp, confirmação e tratamento de indisponibilidade. |
| US08 - Administração | UC09; pp. 7-8, 18 | Must Have | Catálogo na 3; pedidos na 5; mensagens na 6 | Acesso exclusivo admin, cadastrar/editar/remover com confirmação, imagens, estoque, reflexo na loja, pedidos e mensagens. |

Histórico do cliente e endereço salvo são requisitos da US01/US06, mesmo sem casos de uso independentes. Promoção, cupom e consulta de CEP fazem parte de UC07. Não considerá-los implementados apenas porque há campo no formulário ou método no serviço.

## 5. Arquitetura de implementação

Fluxo principal: navegador → interface React → API Spring → serviços de negócio → repositórios JPA → PostgreSQL. A API coordena provedores de pagamento, e-mail, CEP/frete e armazenamento de imagens por interfaces próprias, permitindo simulação explícita em desenvolvimento e integração real em homologação.

### Frontend

- Organizar páginas, componentes reutilizáveis, acesso à API e estado de autenticação/carrinho.
- Rotas: início/catálogo, produto, login, cadastro, recuperação e redefinição de senha, carrinho, checkout, confirmação, meus pedidos, detalhe do pedido, endereços, favoritos, contato e painel administrativo.
- Painel: produtos, edição/cadastro, estoque, pedidos e mensagens, com navegação restrita.
- Layout responsivo com cabeçalho, categorias, busca, identidade visual da loja e rodapé. Priorizar navegação e checkout em celular.
- Tratar carregamento, lista vazia, erro, tentativa novamente e sessão expirada em cada fluxo. Manter formulário preenchido em erros recuperáveis.
- Centralizar chamadas, tradução de erros e expiração da sessão. Proteção visual de rota não substitui autorização na API.
- Garantir rótulos de campos, uso por teclado, foco, contraste, textos alternativos e mensagens de validação compreensíveis.

### Backend e contratos

- Preservar controllers/services/repositories e introduzir DTOs de resposta para carrinho, pedido e administração, evitando expor entidades completas e dados desnecessários.
- Padronizar erros: validação (400), falta de autenticação (401), acesso negado (403), inexistente (404), conflito de e-mail/estoque/estado (409) e falha externa recuperável.
- Documentar contratos, formatos decimais, paginação, autenticação e exemplos de requisição/resposta antes de integrar telas.
- Manter endpoints atuais quando possível. Adicionar recuperação de senha, endereços, orçamento de checkout, pagamento e rotas administrativas.
- Separar criação do pedido, tentativa de pagamento e confirmação assíncrona. Nunca confiar em preço, frete, desconto ou status enviados pelo navegador.
- Usar perfis de ambiente; simulações precisam aparecer claramente nas configurações e telas de demonstração.

### Dados

Reaproveitar Usuario, Endereco, Categoria, Produto, Carrinho, ItemCarrinho, Favorito, Pedido, ItemPedido, Pagamento e Mensagem.

Evoluções propostas:

- Produto: fita de gorgurão, SKU, largura em milímetros, cor/estampa, apresentação, unidade, comprimento quando aplicável, composição e imagens. Variante/SKU independente se necessário. Quantidade mínima/incremento e estoque decimal para venda por metro; estoque em unidades inteiras para cortes fixos ou rolos fechados.
- ItemCarrinho e ItemPedido: quantidade e unidade compatíveis com a apresentação da fita; decimal para metros e inteira para unidades. ItemPedido preserva nome/SKU, largura, cor/estampa, apresentação, comprimento quando aplicável, preço e unidade do momento da compra.
- Pedido: identificador único, endereço copiado, totais, prazo/reserva, rastreio e histórico de status.
- Pagamento: identificador do provedor, tentativas, estado, validade e eventos processados. Permitir nova tentativa sem duplicar pedido.
- Novas estruturas conforme necessidade: recuperação de senha, preferências de catálogo, cupom, reserva/movimentação de estoque e notificações pendentes.
- Versionar alterações do banco; garantir unicidade de e-mail normalizado, carrinho por usuário, favorito por usuário/produto, número de pedido e evento de pagamento.

## 6. Etapas de execução

As etapas seguem dependências, não datas prometidas. Planejar ciclos curtos com demonstração de uma entrega utilizável. Estimar duração após concluir a etapa 0 e resolver a política de venda das fitas de gorgurão; prazo comercial depende dos provedores e do catálogo real.

### Etapa 0 - Ambiente e decisões fundamentais

1. Atualizar a documentação com a arquitetura React/Spring/PostgreSQL já confirmada e com o catálogo inicial exclusivo de fitas de gorgurão.
2. Validar Java/Maven, instalação do frontend, build e conexão PostgreSQL; registrar falhas encontradas.
3. Criar Dockerfile do frontend, configuração de acesso à API e ajustar Compose para executar o conjunto.
4. Documentar como iniciar o projeto; fornecer configuração de exemplo sem segredos reais.
5. Aplicar venda por metro confirmada, parametrizar corte mínimo/incremento e separar demonstração acadêmica de venda comercial.

Saída: ambiente reproduzível, builds executados e decisões básicas registradas. Dependências: nenhuma.

### Etapa 1 - Fundação da aplicação

1. Criar migrações e dados demonstrativos controlados; não substituir dados existentes.
2. Implementar tratamento uniforme de erros, DTOs e validações comuns.
3. Configurar origens permitidas, secrets por ambiente e criação inicial de administrador por procedimento controlado; cadastro público sempre cria cliente.
4. Estruturar frontend, navegação, componentes e cliente da API.
5. Registrar contratos e preparar verificações automatizadas para autenticação, propriedade de dados e regras críticas.

Saída: base navegável, banco versionado e comunicação frontend/backend. Dependências: etapa 0.

### Etapa 2 - Conta e endereços

1. Construir cadastro e login, validar nome/e-mail/telefone/senha e normalizar e-mail.
2. Implementar recuperação por token de uso único e expiração, com envio de e-mail em ambiente de teste.
3. Definir expiração/logout e bloqueio da conta, evitando acesso indevido após mudança de estado.
4. Implementar cadastro, edição, remoção e seleção de endereços da própria conta.
5. Retornar ao fluxo de compra após autenticação e exibir nome do cliente.

Saída: US01 e base de endereços da US06 demonstráveis, incluindo erro e recuperação. Dependências: etapa 1 e serviço de e-mail para validar entrega.

### Etapa 3 - Catálogo de fitas de gorgurão e administração de produtos

1. Adequar produto e quantidades ao modelo de venda das fitas de gorgurão escolhido, migrando dados existentes e restringindo os produtos ofertados ao escopo aprovado.
2. Implementar catálogo paginado, busca, categorias, filtros por características das fitas, detalhe e preservação de filtros.
3. Bloquear exposição pública de produto inativo; exibir produto esgotado conforme política do catálogo.
4. Implementar cadastro/edição/inativação de fitas de gorgurão, suas apresentações/variantes, estoque e imagens no painel. Validar o escopo também na API.
5. Validar formato/tamanho de imagens e armazenar com referências persistentes.
6. Implementar atualização de preço/estoque em tempo real, inclusive mudanças administrativas, usando canal de eventos e validando propagação, conforme decisão confirmada.

Saída: proprietária cadastra fita de gorgurão que aparece corretamente na loja, com largura, cor/estampa, apresentação, unidade e quantidade válida. Dependências: etapa 1; etapa 2 para acesso administrativo.

### Etapa 4 - Carrinho completo

1. Criar carrinho visitante no navegador e carrinho persistido do cliente.
2. Adicionar, editar/remover e recalcular totais, respeitando mínimos/incrementos e estoque.
3. Mesclar carrinhos após login, agregando o mesmo SKU e informando ajustes de estoque ou indisponibilidade.
4. Revalidar produto ativo, preço e estoque ao atualizar carrinho e antes do checkout; apresentar mudanças ao cliente.
5. Garantir isolamento de contas, estado vazio e preservação de itens em falhas.

Saída: jornada catálogo → carrinho → login mantém seleção e valores consistentes. Dependências: etapas 2 e 3.

### Etapa 5 - Checkout, pagamento e operação de pedidos

1. Criar orçamento de checkout no servidor: itens, endereço, CEP, frete/prazo, promoção, cupom e total.
2. Implementar consulta de CEP com preenchimento manual quando indisponível; não confundir CEP existente com garantia de endereço entregável.
3. Validar cupons, prazo, limites e acúmulo com promoção; oferecer mensagem explícita para cupom inválido.
4. Criar pedido pendente com chave de idempotência e snapshot de itens/endereço; reservar estoque atomicamente, inclusive em compras simultâneas.
5. Integrar Pix/cartão/boleto em ambiente de teste. Usar tokenização ou página hospedada do provedor para cartão, evitando trânsito de número completo/CVV pelo backend da loja.
6. Validar autenticidade dos retornos, processar eventos repetidos uma única vez e conciliar estado após falha ou ausência de retorno.
7. Confirmar pagamento somente por evidência do provedor. Liberar reserva em expiração/falha/cancelamento conforme política, sem repor duas vezes.
8. Implementar confirmação por e-mail com reenvio controlado; falha de e-mail não desfaz pedido confirmado.
9. Criar histórico e detalhe do cliente, painel de pedidos, despacho/rastreio e histórico administrativo.

Saída: compra em homologação ponta a ponta com sucesso, recusa, pendência, repetição e expiração testados. Dependências: etapas 2 a 4, regras comerciais e provedores definidos.

Estados propostos: pedido PENDENTE → PAGO → EM_PROCESSAMENTO → ENVIADO → ENTREGUE; cancelamento apenas nas condições permitidas. Pagamento tem ciclo próprio. Cancelamento após pagamento precisa iniciar estorno e ter acompanhamento; não basta alterar o enum do pedido. A autoridade para confirmar pagamento pertence ao processamento validado do provedor; operações administrativas excepcionais exigem trilha de auditoria.

### Etapa 6 - Favoritos e atendimento

1. Integrar favoritos na listagem, detalhe e menu; persistir por cliente e preservar estado diante de falha.
2. Construir contato com validação, confirmação, armazenamento e encaminhamento por e-mail.
3. Configurar WhatsApp com número fornecido pela loja.
4. Disponibilizar consulta de mensagens para administrador. Registrar envio pendente e retentar quando o serviço externo falhar, exibindo confirmação compatível com o que ocorreu.

Saída: US04 e US07 completas; consulta de mensagens do UC09. Dependências: etapas 2 e 3, infraestrutura de e-mail. Podem ser implementadas antes da etapa 5 se a integração comercial estiver aguardando credenciais.

### Etapa 7 - Homologação e publicação

1. Executar testes críticos em PostgreSQL, validações de acesso e jornadas no navegador.
2. Verificar responsividade, teclado, mensagens, atualização de estoque e comportamento com rede indisponível.
3. Configurar ambientes, HTTPS, logs sem dados sensíveis, monitoramento e rotina de backup; testar restauração.
4. Revisar domínio, imagens, contatos, regras comerciais e informações de entrega/troca com a proprietária.
5. Fazer compra de homologação e validar a operação da proprietária, incluindo cancelamento e erro de pagamento.
6. Registrar instruções operacionais, migrações e procedimento de reversão compatível com pedidos já registrados.

Saída: checklist aprovado, execução demonstrada e ambiente preparado para publicação. Dependências: todas as entregas do escopo escolhido. Venda real exige integração validada, não apenas telas e pagamentos simulados.

## 7. Verificação e critérios de conclusão

Cada história deve ter ao menos um cenário de aceite; fluxos alternativos relevantes precisam ser cobertos. Priorizar testes que revelem falhas reais:

| Área | Cenários essenciais |
| --- | --- |
| Conta | E-mail duplicado, formato inválido, credenciais erradas, conta bloqueada, recuperação expirada/reutilizada, sessão expirada e logout. |
| Autorização | Cliente não administra catálogo; usuário A não vê/altera endereço, carrinho ou pedido de B; visitante não finaliza pedido. |
| Catálogo | Lista vazia, busca sem resultado, categoria/filtro, paginação, detalhe inexistente/inativo e mudança de preço/estoque propagada. |
| Fitas de gorgurão | Catálogo/cadastro restritos ao gorgurão, escolha correta de largura/cor/estampa/apresentação, estoque por SKU, quantidade decimal válida para metros, mínimo/incremento inválido, unidade fracionada rejeitada e corte contínuo se aplicável. |
| Carrinho | Mesclagem no login, alteração de preço, produto inativado, estoque reduzido, edição/remoção e isolamento após troca de conta. |
| Checkout | Carrinho vazio, CEP inválido, cotação indisponível, cupom inválido/expirado e total calculado exclusivamente no servidor. |
| Concorrência | Dois clientes disputam o último estoque; apenas a reserva permitida vence. Repetir checkout não cria novo pedido. |
| Pagamento | Cartão recusado, Pix/boleto pendente/expirado, retorno inválido, evento duplicado/fora de ordem e nova tentativa sem duplicação. |
| Pedido | Histórico preserva preço/descrição originais, transição inválida é rejeitada e cancelamento não libera estoque duas vezes. |
| Atendimento/imagens | Mensagem salva com e-mail indisponível, reenvio controlado, upload inválido rejeitado e somente admin recebe dados de contato. |

Uma entrega está concluída quando: funciona na interface e na API; persiste corretamente; trata vazio/erro/permissão; passa os cenários relevantes; possui instrução de execução; e pode ser demonstrada ao responsável. Não marcar história completa por mera existência de serviço ou por teste de tela com dados estáticos.

## 8. Marcos e próximos passos

- Marco A: ambiente reproduzível e decisões de arquitetura/unidades registradas (etapas 0-1).
- Marco B: catálogo real administrável e conta funcionando (etapas 2-3).
- Marco C: jornada de compra integrada em homologação (etapas 4-5). MVP Must Have, sujeito à homologação da etapa 7.
- Marco D: favoritos e atendimento entregues (etapa 6). Escopo funcional completo do PDF, respeitadas as decisões registradas.
- Marco E: operação homologada e publicação preparada (etapa 7).

Primeiro lote recomendado de implementação: validar builds e banco, completar a configuração do frontend no Docker, documentar a arquitetura confirmada e configurar as regras de corte das fitas vendidas por metro. Em seguida, entregar uma fatia integrada: administrador cadastra uma fita de gorgurão → visitante consulta catálogo/detalhe e escolhe a apresentação/variante → adiciona quantidade válida ao carrinho → faz login com mesclagem da seleção. Isso valida o domínio e a comunicação entre camadas antes do investimento na integração de pagamentos.

Fora do escopo inicial: venda de tecidos, fitas de outros tipos e outros artigos para costura/artesanato; pagamento por depósito; aplicativo nativo, múltiplos vendedores, chat em tempo real, fidelidade, recomendações personalizadas e integrações fiscais/ERP não descritas no PDF. Resposta a mensagens dentro do painel é evolução prevista; consulta de mensagens permanece no escopo do UC09.


## 9. Registro da primeira entrega de implementação - 08/10/2026

Unidade confirmada: metro. Cada combinação de largura/cor/estampa é representada por um produto com SKU próprio nesta primeira entrega. Corte mínimo e incremento são campos obrigatórios do cadastro; os dados de demonstração usam 0,50 m e 0,10 m, sem fixar esses valores como regra da loja.

Implementado:

- Ambiente local com perfil `dev` explícito, banco H2 descartável, migrações Flyway e catálogo demonstrativo de seis fitas de gorgurão.
- Banco persistente PostgreSQL com schema versionado e validação, sem alterações automáticas por `ddl-auto=update`. Produtos antigos não são classificados automaticamente como gorgurão.
- Estrutura Docker do frontend, proxy da API e configuração de variáveis sem segredos de exemplo em produção.
- Cadastro/login/logout básicos, e-mail normalizado, autorização de administrador e criação inicial controlada de administrador fora do perfil de demonstração.
- Catálogo com paginação, busca por nome/cor, filtros de categoria/largura e detalhe com disponibilidade, SKU e regras de corte.
- Metragem decimal em produto, carrinho e itens de pedido; totais arredondados em centavos e validação de mínimo/incremento/estoque.
- Carrinho visitante persistido no navegador e carrinho por conta, atualização e remoção de itens.
- Mesclagem transacional após autenticação com chave persistente para evitar duplicação em uma repetição. Em conflito, nenhuma parte da mesclagem é salva; os itens visitantes são preservados para revisão.
- Painel administrativo de cadastro, edição e retirada de fitas. Imagens por URL e ilustração de fallback; upload ainda pendente.
- Atualização de catálogo e detalhes por eventos SSE após confirmação da transação administrativa.
- Checkout desabilitado por padrão na API e na interface enquanto não houver integração real de pagamento. Cartão simulado não gera confirmação automática de pagamento.
- Instruções de execução e contratos da primeira entrega no README.

Pendências para os próximos lotes:

- Definir valores comerciais de mínimo/incremento, política de rolos/corte contínuo, frete, cupons e provedor de pagamento.
- Recuperação de senha por e-mail, bloqueio/revogação de sessão, validação completa de telefone, endereços e preferências de catálogo persistidas por cliente.
- Upload de imagens, favoritos, atendimento/e-mails e gestão de pedidos.
- Checkout real, reserva concorrente de estoque, confirmação pelo provedor, cancelamento/estorno e histórico completo dos itens do pedido.
- Homologação comercial e execução dos containers (Docker Desktop não estava em execução durante esta entrega).

As etapas 0 a 4 foram iniciadas com uma jornada integrada. Nenhuma história inteira deve ser considerada concluída enquanto seus critérios pendentes acima não forem atendidos.

Verificação desta entrega: 11 testes de backend aprovados em H2 e PostgreSQL 18 temporário, 4 testes de frontend aprovados, builds de frontend/backend concluídos, configuração Compose validada e jornada testada em navegador desktop/móvel com atualização em tempo real entre abas. Os artefatos de compilação previamente versionados em `Back-End/target` foram retirados do código-fonte; a pasta passou a ser ignorada pelo Git.


## 10. Ampliação do catálogo - 12/10/2026

O responsável solicitou o cadastro de cinco fitas de cetim e forneceu as respectivas fotos. Esta solicitação amplia a restrição inicial de material: o catálogo e o painel agora aceitam cetim e gorgurão, preservando a identificação correta de cada produto.

Cadastros: CF009 2105 (Azul Oceano), CF009 2101 (Rosa Magnólia), CF009 1377 (Bordô), CF009 1235 (Laranja Abóbora) e CF009 280 (Verde Cítrico), todos com 38 mm de largura. Preço temporário de R$ 4,90/m e estoque temporário de 30 m por cor foram escolhidos com autorização do responsável. Para estas cinco fitas, corte mínimo de 1 m e incremento de 0,5 m foram definidos pelo responsável.

Migração V4 permite os dois materiais. Fotos originais preservadas e vinculadas por SKU. Cadastro da demonstração é idempotente e mantém alterações posteriores feitas pela loja; não inserir dados de teste automaticamente em produção. Valores e cortes continuam configuráveis no painel.
