# 🏦 ApexBank

A full-stack **online banking application** built with a **Spring Boot microservices** backend and an **Angular 17** frontend. It supports customer registration, account management, fund transfers (NEFT & UPI), transaction history, and a full admin panel.

---

<h2>🎥 Demo Video</h2>

https://github.com/user-attachments/assets/80c0a0a3-b119-4af1-a1bd-2bb842fa8193

---

## 🧱 Architecture Overview

```
Angular 17 Frontend
        │
        ▼
  API Gateway (port 8080)   ◄──── JWT Auth Filter
        │
        ├──► Auth Service        (port 8081)
        ├──► Account Service     (port 8082)
        └──► Transaction Service (port 8084)
                │
                └──► Eureka Server (port 8761)
```

All services register with **Eureka** for service discovery. The **API Gateway** validates JWT tokens and forwards user context (`userId`, `accountId`, `roles`) as headers to downstream services.

---

## 🚀 Tech Stack

### Backend
| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.3.2 |
| Cloud | Spring Cloud 2023.0.3 |
| Language | Java 17 |
| Security | JWT (jjwt 0.12.5) |
| Database | MySQL 8 |
| Service Discovery | Netflix Eureka |
| Inter-service | OpenFeign |
| Build | Maven (multi-module) |

### Frontend
| Layer | Technology |
|---|---|
| Framework | Angular 17 |
| Language | TypeScript |
| Styling | CSS / Angular Material |
| Routing | Angular Lazy Loading |

---

## 📦 Microservices

### 1. `eureka-server` — Service Registry
- Runs on port **8761**
- All backend services register here for discovery

### 2. `api-gateway` — Gateway + Auth
- Runs on port **8080**
- Routes all incoming requests to appropriate services
- Validates JWT Bearer tokens globally
- Extracts and forwards `X-Auth-UserId`, `X-Auth-AccountId`, `X-Auth-AccountNumber`, `X-Auth-Roles` headers

### 3. `auth-service` — Authentication
- Runs on port **8081**
- Handles registration, login, OTP, and password management
- Issues JWT tokens (1 hour expiry)
- Max 3 failed login attempts before lockout
- 6-digit OTP with 5-minute expiry

### 4. `account-service` — Account & Admin Management
- Runs on port **8082**
- Customer dashboard, account summary, profile
- Admin panel: approve/reject account opening requests, credit operations, system stats

### 5. `transaction-service` — Fund Transfers
- Runs on port **8084**
- NEFT and UPI fund transfers
- Payee/beneficiary management
- Transaction history and statement generation

---

## 🗄️ Databases

Three separate MySQL databases, one per service:

| Database | Used By |
|---|---|
| `apexbank_auth_db` | auth-service |
| `apexbank_account_db` | account-service |
| `apexbank_txn_db` | transaction-service |

---

## 🔌 API Endpoints

### Auth (`/api/auth`)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/login` | User login, returns JWT |
| POST | `/register` | New user registration |
| POST | `/send-otp` | Send OTP to registered contact |
| POST | `/forgot-user-id` | Retrieve forgotten user ID |
| POST | `/forgot-password/validate-otp` | Validate OTP for password reset |
| POST | `/set-new-password` | Set new password after OTP |
| POST | `/change-password` | Change password (authenticated) |

### Accounts (`/api/accounts`, `/api/dashboard`, `/api/admin`)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/dashboard` | Customer dashboard overview |
| GET | `/api/accounts/summary` | Account summary |
| GET | `/api/accounts/statement` | Account statement |
| GET | `/api/admin/requests` | Pending account opening requests |
| POST | `/api/admin/requests/{id}/approve` | Approve account request |
| POST | `/api/admin/requests/{id}/reject` | Reject account request |
| POST | `/api/admin/credit` | Credit an account |

### Transactions (`/api/fund-transfer`, `/api/payees`)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/fund-transfer/neft` | NEFT fund transfer |
| POST | `/api/fund-transfer/upi` | UPI fund transfer |
| GET | `/api/fund-transfer/recent` | Recent transactions |
| POST | `/api/fund-transfer/statement` | Full transaction statement |
| GET | `/api/payees` | List saved payees |
| POST | `/api/payees` | Add a new payee |
| DELETE | `/api/payees/{id}` | Remove a payee |

---

## 🖥️ Frontend Pages

| Route | Description |
|---|---|
| `/auth/login` | Customer login |
| `/auth/register` | New account registration |
| `/auth/forgot-password` | Password recovery |
| `/dashboard` | Customer home with account overview |
| `/dashboard/statement` | View transaction history |
| `/dashboard/fund-transfer` | Transfer funds (NEFT / UPI) |
| `/dashboard/payees` | Manage beneficiaries |
| `/dashboard/profile` | Profile settings |
| `/admin/login` | Admin login |
| `/admin/dashboard` | Admin home |
| `/admin/requests` | Account opening requests |
| `/admin/users` | Manage users |
| `/admin/transactions` | View all transactions |
| `/admin/credit` | Credit customer accounts |

---

## ⚙️ Running Locally

### Prerequisites
- Java 17+
- Node.js 18+ & npm
- Docker & Docker Compose

### 1. Start the backend with Docker

```bash
cd backend
docker-compose up --build
```

This starts MySQL and all 5 Spring Boot services.

### 2. Start the frontend

```bash
cd frontend
npm install
ng serve
```

Frontend runs on `http://localhost:4200`  
API Gateway runs on `http://localhost:8080`  
Eureka Dashboard: `http://localhost:8761`

---

## 🔐 Security

- JWT-based stateless authentication
- Tokens validated at the API Gateway — no auth logic in downstream services
- Role-based access control (`CUSTOMER`, `ADMIN`) enforced via gateway headers
- Transaction password verified by auth-service via internal Feign call
- Account lockout after 3 failed login attempts
- OTP-based flows for registration and password reset

---

## 📁 Project Structure

```
apexbank/
├── backend/
│   ├── pom.xml                  # Maven multi-module parent
│   ├── docker-compose.yml
│   ├── eureka-server/
│   ├── api-gateway/
│   ├── auth-service/
│   ├── account-service/
│   └── transaction-service/
└── frontend/
    └── src/
        └── app/
            ├── auth/
            ├── dashboard/
            └── admin/
```

---
