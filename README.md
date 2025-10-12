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

If the remote DB configured in `application.properties` is not reachable, the application may still start (depending on Hibernate settings) but endpoints that access the DB will fail with SQL/connection errors.

To override datasource settings at runtime (temporary, without changing files):

```bash
SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/mydb' \
SPRING_DATASOURCE_USERNAME='user' \
SPRING_DATASOURCE_PASSWORD='pass' \
./mvnw spring-boot:run
```

This is useful when you have a local Postgres instance and want to avoid hitting the remote DB.

If you prefer an in-memory DB (H2) for quick testing, a `local` profile using H2 can be added—ask me and I can add it for you.

## Available endpoints (quick reference)

- GET  /admin/getAllDoctors  — returns list of doctors
- POST /doctor/addOrUpdate   — accepts JSON DoctorMaster and saves it
- GET  /doctor/getById?doctorId=ID — returns a doctor by id
- POST /patient/addOrUpdate  — accepts JSON PatientMaster and saves it
- GET  /patient/getById?patientId=ID — returns a patient by id

Entity field names:

- DoctorMaster: `doctorMasterId`, `doctorName`, `doctorEmail`, `doctorPassword`
- PatientMaster: `patientMasterId`, `patientName`

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

## Want an H2/local profile?
If you'd like, I can add a `application-local.properties` and a `spring.profiles.active=local` example that uses an embedded H2 database and (optionally) inserts sample data so you can test without a remote Postgres. Reply: `Add H2 profile` and I'll implement it.

## Last notes
- The project uses Spring Security but current `SecurityConfig` permits all requests by default for this repository, so endpoints should not require authentication.
- If you run into errors, paste the startup logs or the `curl -v` output and I will help diagnose.

---
Generated instructions for macOS (zsh). If you want the README adjusted for Windows or Linux, say so.
