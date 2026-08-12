// In the microservices setup, the Angular app talks ONLY to the API Gateway
// (port 8080). The Gateway routes /api/auth/** to auth-service,
// /api/accounts|dashboard|admin/** to account-service, and
// /api/fund-transfer|payees/** to transaction-service — the frontend
// doesn't need to know which service actually handles each call.
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
