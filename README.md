# Wecureit_be — Backend (Spring Boot)

This README explains how to clone, run, and smoke-test the `wecureit_be` Spring Boot backend on macOS (zsh).

## Prerequisites
- Java 21 JDK installed and `JAVA_HOME` configured. Check with:

```bash
java -version
echo $JAVA_HOME
```

- Git (to clone the repo)
- The project contains a Maven wrapper (`mvnw`) so a system Maven install is not required.

## Clone the repository

Run from the directory where you keep projects:

```bash
cd ~/Documents/Projects
git clone <REPO-URL> wecureit_be
cd wecureit_be
```

Replace `<REPO-URL>` with your repository URL (SSH or HTTPS).

## Run the application (development)

Make the Maven wrapper executable (one-time):

```bash
chmod +x ./mvnw
```

Start the application (foreground, shows logs):

```bash
./mvnw spring-boot:run
```

Look for a log line like:

```
Tomcat started on port(s): 8080 (http)
Started WecureitBeApplication
```

Keep this terminal open while you work; it prints request logs and errors.

## Build and run the packaged jar

```bash
./mvnw -DskipTests package
java -jar target/wecureit_be-0.0.1-SNAPSHOT.jar
```

To run the jar in background and keep logs:

```bash
nohup java -jar target/wecureit_be-0.0.1-SNAPSHOT.jar > wecureit.log 2>&1 &
tail -f wecureit.log
```

## Database and configuration notes

By default the application reads `src/main/resources/application.properties` and will attempt to connect to the PostgreSQL database configured there.

To override datasource settings at runtime (temporary, without changing files):

```bash
SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/mydb' \
SPRING_DATASOURCE_USERNAME='user' \
SPRING_DATASOURCE_PASSWORD='pass' \
./mvnw spring-boot:run
```

## Available endpoints (quick reference)

- GET  /admin/getAllDoctors  — returns list of doctors
- POST /doctor/addOrUpdate   — accepts JSON DoctorMaster and saves it
- GET  /doctor/getById?doctorId=ID — returns a doctor by id
- POST /patient/addOrUpdate  — accepts JSON PatientMaster and saves it
- GET  /patient/getById?patientId=ID — returns a patient by id

Entity field names:

- DoctorMaster: `doctorMasterId`, `doctorName`, `doctorEmail`, `doctorPassword`
- PatientMaster: `patientMasterId`, `patientName`

## Program Flow (startup and request lifecycle)

This section explains how the backend starts and how an incoming HTTP request travels through the application to the database and back.

Startup sequence (high level):

- `com.example.wecureit_be.WecureitBeApplication` — Spring Boot application entry point (main). Spring Boot starts the application context, auto-configures beans and embedded Tomcat.
- `SecurityConfig.java` (and other `@Configuration` classes like `WebConfig.java`) — configuration classes are processed, security and web configuration beans are registered.
- Spring scans and registers controllers (`@RestController`), services/impl classes (`@Service`), repositories (`@Repository`) and entities (`@Entity`).

Request lifecycle (example overview):

1. HTTP client calls an endpoint, e.g.:

GET http://localhost:8080/doctor/getById?doctorId=1001

2. DispatcherServlet receives the request and routes it to the correct controller based on the request path and HTTP method.

3. Controller method parameter binding: Spring MVC extracts the query parameter `doctorId` and converts it to the controller method parameter annotated with `@RequestParam`.
- In this project: `DoctorController#getById(@RequestParam Integer doctorId)`.

4. Controller forwards the call to the implementation/service layer:
- `DoctorController` calls `DoctorMasterImpl.getById(doctorId)`.

5. Implementation layer calls the repository method which executes the SQL/JPA query:
- `DoctorMasterRepository.getDoctorById(@Param("doctorId") Integer doctorId)` is invoked.
- The repository method is annotated with `@Query(value = "SELECT * FROM doctor_master WHERE doctor_master_id = :doctorId", nativeQuery = true)` so Spring Data JPA binds the method parameter `doctorId` to the named SQL parameter `:doctorId` and executes the parameterized query against the database.

6. Database returns a row (or none). Spring Data JPA maps the result to the `DoctorMaster` entity (`@Entity`) and returns it to the caller.

7. The controller returns the `DoctorMaster` object. Spring Boot uses Jackson to serialize the entity to JSON and writes the HTTP response back to the client.

Files involved (mapping to the codebase):

- `src/main/java/com/example/wecureit_be/WecureitBeApplication.java` — application entry point
- `src/main/java/com/example/wecureit_be/config/SecurityConfig.java` — security configuration
- `src/main/java/com/example/wecureit_be/config/WebConfig.java` — web config (CORS, formatters, etc.)
- `src/main/java/com/example/wecureit_be/controller/DoctorController.java` — controller that handles `/doctor` endpoints
- `src/main/java/com/example/wecureit_be/impl/DoctorMasterImpl.java` — service/impl layer that orchestrates repository calls
- `src/main/java/com/example/wecureit_be/entity/DoctorMaster.java` — JPA entity mapping to the `doctor_master` table
- `src/main/java/com/example/wecureit_be/repository/DoctorMasterRepository.java` — Spring Data JPA repository with the `@Query` method

Concrete example mapped to code (GET /doctor/getById?doctorId=1001):

- HTTP request URL: `/doctor/getById?doctorId=1001`
- Controller: `DoctorController.getById` receives Integer `doctorId = 1001` via `@RequestParam`.
- Impl: `DoctorMasterImpl.getById(1001)` calls the repository.
- Repository: `getDoctorById(1001)` binds `:doctorId = 1001` into the SQL and executes it.
- DB row maps to `DoctorMaster` entity and is returned to the client as JSON.

Notes and gotchas:

- `@RequestParam` is required by default. If the caller omits `doctorId`, Spring will return 400 Bad Request unless you set `required = false` or provide `defaultValue`.
- Parameter binding is done safely using JDBC prepared statements under the hood; values are not concatenated into SQL strings.
- If your entity's `@Id` type is `Integer`, make sure repository generics match (e.g., `JpaRepository<DoctorMaster, Integer>`).
- Consider returning `Optional<DoctorMaster>` from repository methods if you want to express "not found" explicitly rather than returning `null`.

## Smoke tests (curl examples)

Run these once the app is started and listening on `localhost:8080`.

- Check all doctors:

```bash
curl -i http://localhost:8080/admin/getAllDoctors
```

- Add or update a doctor:

```bash
curl -i -X POST http://localhost:8080/doctor/addOrUpdate \
  -H "Content-Type: application/json" \
  -d '{"doctorMasterId":"doc1","doctorName":"Dr Alice","doctorEmail":"alice@example.com","doctorPassword":"secret"}'
```

- Get doctor by id:

```bash
curl -i "http://localhost:8080/doctor/getById?doctorId=doc1"
```

- Add or update a patient:

```bash
curl -i -X POST http://localhost:8080/patient/addOrUpdate \
  -H "Content-Type: application/json" \
  -d '{"patientMasterId":"pat1","patientName":"Bob"}'
```

- Get patient by id:

```bash
curl -i "http://localhost:8080/patient/getById?patientId=pat1"
```

## Troubleshooting

- Permission denied running `./mvnw`: run `chmod +x ./mvnw`.
- Port 8080 already in use: find and kill the process or change server port using `-Dserver.port=9090` or `server.port=9090` in properties.
- Database connection errors: verify DB URL, username, password and network connectivity; use the environment override to point to a reachable DB for local testing.
- 404 responses: confirm the exact path and HTTP method.
- 500 responses: check the app logs (where you ran `./mvnw spring-boot:run` or `wecureit.log`) for stack traces.

## Run from an IDE

Import the project as a Maven project (IntelliJ IDEA or Eclipse). Set the project SDK to Java 21. Run the main class `com.example.wecureit_be.WecureitBeApplication`.

How to backup the database and run locally:
Here’s the **exact README section** you can add — it documents how to connect Amazon RDS to pgAdmin, create local backups, and configure your Spring Boot app for both environments.

---

## Database Setup and Backup — WeCureIT

### 1. Connect to Amazon RDS from pgAdmin

1. **Open pgAdmin 4**

   * Launch pgAdmin and expand the left sidebar (“Servers”).

2. **Add a New Server**

   * Right-click **Servers → Create → Server**
   * Under the **General tab**, name it: `WeCureIT-RDS`.

3. **Configure Connection Tab**

   | Field                | Example Value                                       |
   | -------------------- | --------------------------------------------------- |
   | Host name / address  | `wecureit.ca18uqsmuuzk.us-east-1.rds.amazonaws.com` |
   | Port                 | `5432`                                              |
   | Maintenance database | `wecureit`                                          |
   | Username             | `wecureit_db`                                       |
   | Password             | `wecureit_db`                                       |
   | SSL Mode             | `Require`                                           |

   Click **Save**.
   pgAdmin will now connect to your AWS RDS PostgreSQL instance.

---

### 2. Create a Local Backup from RDS

1. **Right-click on Database → Backup**

   * Choose the **RDS database** (`wecureit`).
   * Select **Backup...**.

2. **In the Backup dialog:**

   * **Format:** Custom (`.dump`)
   * **Filename:**
     `/Users/<your-username>/rds_backups/wecureit_$(date).dump`
   * **Encoding:** UTF-8
   * Leave other settings default.

3. Click **Backup**.

   Your backup file (`.dump`) will be created locally (in the chosen folder).

---

### 3. Restore the Backup to Local PostgreSQL

1. **Connect to Local Server in pgAdmin**

   * Right-click **Servers → Create → Server**
   * Use:

     * Host: `localhost`
     * Port: `5432`
     * Username: your macOS PostgreSQL username (e.g., `ullasbc`)
     * Password: (same as local)

2. **Create Empty Database**

   * Right-click **Databases → Create → Database**
   * Name: `wecureit`

3. **Restore**

   * Right-click **wecureit → Restore...**
   * Select the `.dump` file you backed up earlier.
   * Format: **Custom**
   * Check *“Clean before restore”* (optional).
   * Click **Restore**.

If successful, you’ll see all `doctor_master`, `facility_master`, etc. tables under **Schemas → public → Tables**.

---


## Spring Boot Configuration

### `application.properties`

```properties
spring.application.name=wecureit_be
spring.profiles.active=local  # or prod
```




