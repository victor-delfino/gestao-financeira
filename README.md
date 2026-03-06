# 💰 Gestão Financeira

Projeto educacional de gestão financeira pessoal, construído com **Arquitetura Hexagonal** (Ports & Adapters) no backend e **React + TypeScript** no frontend.

O objetivo é demonstrar boas práticas de engenharia de software: separação de camadas, inversão de dependências, domínio rico com validações, autenticação JWT stateless e testes automatizados.

---

## 📋 Índice

- [Tech Stack](#-tech-stack)
- [Arquitetura](#-arquitetura)
- [Pré-requisitos](#-pré-requisitos)
- [Como Rodar](#-como-rodar)
- [API Endpoints](#-api-endpoints)
- [Modelo de Domínio](#-modelo-de-domínio)
- [Testes](#-testes)
- [Estrutura do Projeto](#-estrutura-do-projeto)

---

## 🛠 Tech Stack

### Backend

| Tecnologia | Versão | Propósito |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.2.3 | Framework web + DI |
| Spring Security | 6.x | Autenticação e autorização |
| Spring Data JPA | 3.x | Persistência (Hibernate) |
| PostgreSQL | 16 | Banco de dados relacional |
| JJWT | 0.12.6 | Geração e validação de tokens JWT |
| Lombok | — | Redução de boilerplate (DTOs/JPA) |
| H2 | — | Banco em memória para testes |
| JUnit 5 + Mockito | — | Testes unitários e de integração |
| Maven | 3.9.6 | Build e gerenciamento de dependências |

### Frontend

| Tecnologia | Versão | Propósito |
|---|---|---|
| React | 19 | Biblioteca de UI |
| TypeScript | 5.8 | Tipagem estática |
| Vite | 6 | Build tool + dev server |
| Tailwind CSS | 4.2 | Estilização utilitária |
| Axios | 1.13 | Cliente HTTP |
| React Router DOM | 7.13 | Roteamento SPA |

---

## 🏛 Arquitetura

O backend segue a **Arquitetura Hexagonal** (Ports & Adapters), isolando o domínio de qualquer framework:

```
┌─────────────────────────────────────────────────────────────┐
│                      INFRASTRUCTURE                         │
│  SecurityConfig · BeanConfiguration · JwtTokenProvider      │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                      ADAPTERS                         │  │
│  │                                                       │  │
│  │  ┌─────────────┐               ┌──────────────────┐  │  │
│  │  │  IN (Web)   │               │  OUT (Persistence)│  │  │
│  │  │             │               │                   │  │  │
│  │  │ Controllers │               │  JPA Adapters     │  │  │
│  │  │ DTOs        │               │  Entities         │  │  │
│  │  │ Exception   │               │  Mappers          │  │  │
│  │  │ Handler     │               │  Repositories     │  │  │
│  │  └──────┬──────┘               └────────┬──────────┘  │  │
│  │         │                               │             │  │
│  │         │ usa Port IN          implementa Port OUT     │  │
│  │         ▼                               ▲             │  │
│  │  ┌─────────────────────────────────────────────────┐  │  │
│  │  │               APPLICATION                       │  │  │
│  │  │                                                 │  │  │
│  │  │  TransactionService · UserService               │  │  │
│  │  │  CreateTransactionCommand · RegisterUserCommand │  │  │
│  │  └────────────────────┬────────────────────────────┘  │  │
│  │                       │                               │  │
│  │                       │ usa Domain                    │  │
│  │                       ▼                               │  │
│  │  ┌─────────────────────────────────────────────────┐  │  │
│  │  │                  DOMAIN                         │  │  │
│  │  │                                                 │  │  │
│  │  │  Transaction · User · TransactionType           │  │  │
│  │  │  DomainException                                │  │  │
│  │  │  Ports IN:  CreateTransaction, ListTransactions │  │  │
│  │  │             CalculateBalance, RegisterUser      │  │  │
│  │  │             AuthenticateUser                    │  │  │
│  │  │  Ports OUT: TransactionRepository, UserRepo     │  │  │
│  │  │             TokenPort, PasswordEncoderPort      │  │  │
│  │  └─────────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

**Regra de dependência:** O domínio não conhece nenhum framework. As dependências sempre apontam para dentro (adapters → application → domain).

---

## ✅ Pré-requisitos

- [Java 21](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/)
- [Node.js 18+](https://nodejs.org/) (com npm)
- [Docker](https://www.docker.com/) (para o PostgreSQL)

---

## 🚀 Como Rodar

### 1. Clonar o repositório

```bash
git clone https://github.com/victor-delfino/gestao-financeira.git
cd gestao-financeira
```

### 2. Subir o PostgreSQL (Docker)

```bash
docker compose up -d
```

Isso cria um container PostgreSQL 16 com:
- **Host:** `localhost:5433`
- **Banco:** `gestao_financeira`
- **Usuário:** `postgres` / **Senha:** `postgres`

### 3. Rodar o Backend

```bash
cd gestao-financeira
mvn spring-boot:run
```

O servidor inicia em **http://localhost:8080**. As tabelas são criadas automaticamente (`ddl-auto: update`).

### 4. Rodar o Frontend

```bash
cd frontend
npm install
npm run dev
```

Acesse **http://localhost:5173** no navegador.

### 5. Usar a aplicação

1. Acesse `http://localhost:5173`
2. Crie uma conta na tela de **Registro**
3. Faça login com email e senha
4. Adicione transações (receitas e despesas) no dashboard
5. Acompanhe o saldo no card de balanço

---

## 📡 API Endpoints

### Autenticação (`/api/auth`) — público

| Método | Endpoint | Body | Resposta |
|---|---|---|---|
| `POST` | `/api/auth/register` | `{ name, email, password }` | `{ token, name, email }` — 201 |
| `POST` | `/api/auth/login` | `{ email, password }` | `{ token, name, email }` — 200 |

### Transações (`/api/transactions`) — requer JWT

Enviar header: `Authorization: Bearer <token>`

| Método | Endpoint | Body | Resposta |
|---|---|---|---|
| `POST` | `/api/transactions` | `{ description, amount, type, category, date }` | `TransactionResponse` — 201 |
| `GET` | `/api/transactions` | — | `List<TransactionResponse>` — 200 |
| `GET` | `/api/transactions/balance` | — | `BigDecimal` — 200 |

**Tipos de transação:** `INCOME` (receita) ou `EXPENSE` (despesa)

### Exemplo com cURL

```bash
# Registrar
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"João","email":"joao@email.com","password":"123456"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"joao@email.com","password":"123456"}'

# Criar transação (usar o token retornado no login)
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"description":"Salário","amount":5000,"type":"INCOME","category":"Trabalho","date":"2026-03-01"}'

# Listar transações
curl http://localhost:8080/api/transactions \
  -H "Authorization: Bearer <TOKEN>"

# Consultar saldo
curl http://localhost:8080/api/transactions/balance \
  -H "Authorization: Bearer <TOKEN>"
```

---

## 🏗 Modelo de Domínio

### Transaction

| Campo | Tipo | Validação |
|---|---|---|
| `id` | `UUID` | Gerado automaticamente |
| `userId` | `UUID` | Obrigatório (vinculado ao usuário autenticado) |
| `description` | `String` | Obrigatório, não pode ser vazio |
| `amount` | `BigDecimal` | Obrigatório, deve ser > 0 |
| `type` | `TransactionType` | `INCOME` ou `EXPENSE` |
| `category` | `String` | Obrigatório, não pode ser vazio |
| `date` | `LocalDate` | Obrigatório |

Métodos de negócio: `isIncome()`, `isExpense()`

### User

| Campo | Tipo | Validação |
|---|---|---|
| `id` | `UUID` | Gerado automaticamente |
| `name` | `String` | Obrigatório, não pode ser vazio |
| `email` | `String` | Obrigatório, deve conter `@` |
| `password` | `String` | Armazenado como hash BCrypt |

> As entidades de domínio são **imutáveis** — todas as validações ocorrem no construtor. Se alguma regra for violada, uma `DomainException` é lançada.

---

## 🧪 Testes

O projeto possui **32 testes** cobrindo todas as camadas:

```bash
cd gestao-financeira
mvn test
```

| Classe de Teste | Tipo | O que testa |
|---|---|---|
| `TransactionTest` | Unitário | Validações do domínio (construtor, regras de negócio) |
| `TransactionServiceTest` | Unitário (Mockito) | Casos de uso (criar, listar, calcular saldo) |
| `TransactionControllerTest` | Integração (`@SpringBootTest`) | Endpoints REST com banco H2 |

Os testes de integração usam um perfil `test` com banco **H2 em memória**, não precisam do PostgreSQL.

---

## 📁 Estrutura do Projeto

```
gestao-financeira/              ← raiz do monorepo
│
├── docker-compose.yml          ← PostgreSQL 16 (Docker)
├── .gitignore
├── README.md
│
├── gestao-financeira/          ← backend (Spring Boot)
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/gestao/financeira/
│       │   ├── domain/
│       │   │   ├── model/          Transaction, User, TransactionType
│       │   │   ├── exception/      DomainException
│       │   │   └── port/
│       │   │       ├── in/         Use cases (interfaces)
│       │   │       └── out/        Repository & infra ports (interfaces)
│       │   ├── application/
│       │   │   └── service/        TransactionService, UserService, Commands
│       │   ├── adapters/
│       │   │   ├── in/web/         Controllers, DTOs, ExceptionHandler
│       │   │   └── out/persistence/ JPA entities, mappers, repositories
│       │   └── infrastructure/
│       │       ├── config/         BeanConfiguration, WebConfig
│       │       └── security/       SecurityConfig, JWT, BCrypt
│       └── test/                   32 testes (unitários + integração)
│
└── frontend/                   ← frontend (React + Vite)
    ├── package.json
    └── src/
        ├── components/         BalanceCard, TransactionForm, TransactionList
        ├── contexts/           AuthContext (estado global de autenticação)
        ├── pages/              LoginPage, RegisterPage
        ├── services/           authService, transactionService (Axios)
        ├── types/              transaction.ts (interfaces TypeScript)
        ├── App.tsx             Rotas + Dashboard
        └── main.tsx            Entry point (BrowserRouter + AuthProvider)
```

---

## 📄 Licença

Projeto educacional — uso livre para estudo e aprendizado.
