# Interface Geração Livre

Interface React para venda de fitas de gorgurão por metro. Veja o [README principal](../README.md) para execução, conta demonstrativa, configuração de banco e pendências.

```powershell
npm ci
npm start
```

A interface usa http://localhost:3000 e encaminha `/api` ao backend local na porta 8080. No Docker, Nginx encaminha para o serviço `backend`. Para outra origem de API, defina `REACT_APP_API_URL` no build.

```powershell
npm test -- --watchAll=false --runInBand
npm run build
```
