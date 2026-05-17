# Activity API — devinuser7

A Spring Boot REST API for logging and querying user activity events such as login, logout, create, update, and delete.

## Tech Stack

- Java 17
- Spring Boot 3.x
- Maven
- Jackson (JSON serialization with Java Time support)
- JUnit 5 + MockMvc + AssertJ (testing)

## How It Works

The API uses a file-based activity log (`src/data/activity.log`) to persist user actions as JSON entries. On startup, if the log file does not exist, it is created with 10 sample entries.

Each log entry contains:

| Field       | Description                          |
|-------------|--------------------------------------|
| `userId`    | Identifier of the user               |
| `action`    | Action performed (e.g. LOGIN, CREATE) |
| `timestamp` | ISO 8601 date-time of the action     |

## API Endpoints

### Query Activity Logs

```
GET /api/activity
```

Returns all logged activities. Supports optional query parameters for filtering:

| Parameter | Required | Description                              |
|-----------|----------|------------------------------------------|
| `userId`  | No       | Filter logs by user ID (exact match)     |
| `action`  | No       | Filter logs by action (case-insensitive) |

**Responses:**
- `200 OK` — Returns a JSON array of matching log entries
- `400 Bad Request` — No logs found matching the filter criteria

**Examples:**

```bash
# Get all logs
curl http://localhost:8080/api/activity

# Filter by user
curl http://localhost:8080/api/activity?userId=U101

# Filter by action
curl http://localhost:8080/api/activity?action=LOGIN

# Filter by both
curl http://localhost:8080/api/activity?userId=U101&action=LOGIN
```

### User Login

```
POST /api/users/login
```

Logs a LOGIN action for the specified user.

**Request Body:**
```json
{ "userId": "U101" }
```

**Response:**
```json
{ "message": "User U101 logged in" }
```

### User Logout

```
POST /api/users/logout
```

Logs a LOGOUT action for the specified user.

**Request Body:**
```json
{ "userId": "U101" }
```

**Response:**
```json
{ "message": "User U101 logged out" }
```

### Create User

```
POST /api/users
```

Logs a CREATE action for the specified user.

**Request Body:**
```json
{ "userId": "U300" }
```

**Response:**
```json
{ "message": "User U300 created" }
```

### Update User

```
PUT /api/users/{userId}
```

Logs an UPDATE action for the specified user.

**Request Body:**
```json
{ "name": "Updated Name" }
```

**Response:**
```json
{ "message": "User U300 updated" }
```

### Delete User

```
DELETE /api/users/{userId}
```

Logs a DELETE action for the specified user.

**Response:**
```json
{ "message": "User U300 deleted" }
```

## Use Cases

1. **User Session Tracking** — Track when users log in and log out to monitor session activity and usage patterns.

2. **Audit Trail** — Maintain a chronological record of all user actions (create, update, delete) for compliance and auditing purposes.

3. **Activity Filtering** — Query the activity log by user or action type to investigate specific user behavior or find all occurrences of a particular operation.

4. **User Lifecycle Management** — Monitor the full lifecycle of a user from creation through login, updates, and eventual deletion.

5. **Usage Analytics** — Analyze logged activities to understand which actions are performed most frequently across the system.

## Running the Application

```bash
cd apps/devinuser7
mvn spring-boot:run
```

The API starts on `http://localhost:8080`.

## Running Tests

```bash
cd apps/devinuser7
mvn test
```

The test suite includes:

- **ActivityControllerTest** — Integration tests for the activity query endpoint
- **ActivityLoggerTest** — Unit tests for the service layer (null checks, edge cases)
- **UserControllerTest** — Integration tests for user endpoints (null checks, edge cases)
- **EndToEndFlowTest** — Full user lifecycle test (create, login, update, logout, delete, verify)
