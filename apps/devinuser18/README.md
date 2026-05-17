# Expenses API — devinuser18

A Spring Boot REST API for managing personal expenses, built with Java 17, Spring Data JPA, and an H2 in-memory database.

## Quick start

```bash
mvn spring-boot:run
```

The application starts on **http://localhost:8080**.

## Endpoints

| Method   | Path                          | Request Body / Params              | Response                | Status Codes       |
|----------|-------------------------------|------------------------------------|-------------------------|--------------------|
| `GET`    | `/api/expenses`               | —                                  | `List<Expense>`         | 200                |
| `GET`    | `/api/expenses?category={c}`  | query param `category`             | `List<Expense>`         | 200, 404           |
| `GET`    | `/api/expenses?month={m}`     | query param `month` (1-12)         | `List<Expense>`         | 200, 400, 404      |
| `GET`    | `/api/expenses?category={c}&month={m}` | query params `category`, `month` | `List<Expense>` | 200, 400, 404      |
| `POST`   | `/api/expenses`               | JSON `Expense` body                | `Expense`               | 201, 400           |
| `PUT`    | `/api/expenses/{id}`          | JSON `Expense` body                | `Expense`               | 200, 400, 404      |
| `DELETE` | `/api/expenses/{id}`          | —                                  | —                       | 204, 404           |

### Expense JSON schema

```json
{
  "id": 1,
  "amount": 25.50,
  "category": "Food",
  "description": "Lunch",
  "expenseDate": "2025-05-15"
}
```

## Running tests

```bash
mvn test
```

## H2 console

| Property    | Value                              |
|-------------|------------------------------------|
| URL         | http://localhost:8080/h2-console   |
| JDBC URL    | `jdbc:h2:mem:expensesdb`           |
| Username    | `sa`                               |
| Password    | *(empty)*                          |

## Actuator URLs

| Endpoint                                  | Description               |
|-------------------------------------------|---------------------------|
| http://localhost:8080/actuator/health      | Application health status |
| http://localhost:8080/actuator/info        | Application info          |

## Architecture diagram

```
┌────────────┐       ┌───────────────────┐       ┌─────────────────┐       ┌──────────┐
│   Client   │──────▶│ ExpenseController │──────▶│ ExpenseService  │──────▶│   H2 DB  │
│  (HTTP)    │◀──────│  @RestController  │◀──────│   @Service      │◀──────│ In-Memory│
└────────────┘       └───────────────────┘       └─────────────────┘       └──────────┘
                              │                          │
                              ▼                          ▼
                     ┌───────────────────┐      ┌─────────────────┐
                     │GlobalException    │      │ExpenseRepository│
                     │Handler           │      │ @Repository     │
                     │@RestControllerAdv│      │ (Spring Data    │
                     │ice               │      │  JPA)           │
                     └───────────────────┘      └─────────────────┘
```
