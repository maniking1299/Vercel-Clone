# VDeploy — Cloud Deployment Platform

A simplified deployment platform built from scratch in Java. Users submit a GitHub repository URL, and the system automatically clones it, runs the build inside a Docker container, uploads the output to AWS S3, and returns a live public URL.

**Live Demo:** [https://vercel-clone-manish.s3.ap-south-1.amazonaws.com/deployments/4/index.html](https://vercel-clone-manish.s3.ap-south-1.amazonaws.com/deployments/4/index.html)

**My Portfolio:** [https://vercel-clone-manish.s3.ap-south-1.amazonaws.com/deployments/7/index.html](https://vercel-clone-manish.s3.ap-south-1.amazonaws.com/deployments/7/index.html)

**Live Server:** [http://13.235.27.137:8080](http://13.235.27.137:8080)

---

## What It Does

1. User registers and logs in via a web UI
2. User submits a GitHub repo URL, build command, and output directory
3. The system clones the repo, runs the build inside a Docker container
4. Build output is uploaded to AWS S3
5. A live public URL is returned and stored in the database
6. User can track deployment status in real time (QUEUED → RUNNING → SUCCESS/FAILED)

---

## Architecture

```
Browser (HTML/CSS/JS)
        │
        ▼
Spring Boot REST API (Port 8080)
        │
        ├── JWT Authentication (Spring Security)
        │
        ├── PostgreSQL (Users + Deployments)
        │
        └── LinkedBlockingQueue (Async Engine)
                │
                ▼
        DeploymentWorker (Background Thread)
                │
                ├── git clone → Docker build (ProcessBuilder)
                │
                └── AWS S3 Upload → Live URL
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT |
| Async Engine | LinkedBlockingQueue + Background Thread |
| Containerization | Docker (via ProcessBuilder) |
| Cloud Storage | AWS S3 SDK v2 |
| Build Tool | Maven |
| Deployment | AWS EC2 (Ubuntu, t3.micro) |
| Utilities | Lombok, Slf4j |

---

## Project Structure

```
com.manish.vercelclone
├── config/          # SecurityConfig (JWT + Spring Security)
├── controller/      # REST endpoints (User, Deployment, Health)
├── dto/             # Request/Response objects with validation
├── entity/          # JPA entities (User, Deployment, DeploymentStatus)
├── exception/       # Global exception handler (@RestControllerAdvice)
├── filter/          # JWT filter (OncePerRequestFilter)
├── queue/           # DeploymentQueue + DeploymentWorker
├── repository/      # Spring Data JPA repositories
├── service/         # Business logic (UserService, DeploymentService, S3Service)
└── util/            # JwtUtil (generate, extract, validate)
```

---

## API Endpoints

### Auth
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/users/register` | Register new user | Public |
| POST | `/users/login` | Login, returns JWT token + userId | Public |

### Deployments
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/deploy?userId={id}` | Create new deployment | JWT |
| GET | `/deploy/{id}` | Get deployment by ID | JWT |
| GET | `/deploy/user/{userId}` | Get all deployments for user | JWT |

### Health
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/health` | Server health check | Public |

---

## Key Technical Decisions

**Async Deployment Engine**
Deployments run in a background thread using `LinkedBlockingQueue`. When a user submits a deployment, it's saved to the database with status `QUEUED` and the ID is added to the queue. The `DeploymentWorker` thread continuously picks from the queue and processes deployments without blocking the HTTP request.

**Docker Isolation**
Each build runs inside an isolated Docker container using `ProcessBuilder`. This prevents builds from affecting each other or the host server. The container is automatically removed after the build (`--rm` flag).

**JWT Authentication**
All protected endpoints require a JWT token in the `Authorization: Bearer <token>` header. The `JwtFilter` validates the token on every request before it reaches the controller.

**S3 File Upload**
After a successful build, the output directory is uploaded to S3 with keys in the format `deployments/{id}/{filename}`. Windows backslashes are converted to forward slashes to ensure correct S3 key formatting.

**BCrypt Password Hashing**
Passwords are hashed with BCrypt before storage. Login uses `BCryptPasswordEncoder.matches()` — passwords are never stored or compared in plain text.

---

## Running Locally

### Prerequisites
- Java 21
- Maven 3.9+
- Docker Desktop
- PostgreSQL

### Setup

```bash
# Clone the repo
git clone https://github.com/maniking1299/Vercel-Clone.git
cd Vercel-Clone

# Create application.properties (not committed to git)
nano src/main/resources/application.properties
```

Add the following to `application.properties`:

```properties
spring.application.name=vercelclone

spring.datasource.url=jdbc:postgresql://localhost:5432/vercel_clone
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=YOUR_32_CHAR_SECRET_KEY

aws.accessKeyId=YOUR_AWS_ACCESS_KEY
aws.secretKey=YOUR_AWS_SECRET_KEY
aws.region=ap-south-1
aws.s3.bucketName=YOUR_BUCKET_NAME
```

```bash
# Build and run
mvn clean package -DskipTests
java -jar target/vercelclone-0.0.1-SNAPSHOT.jar
```

Open `http://localhost:8080` in your browser.

---

## Deployment (AWS EC2)

The application is deployed on AWS EC2 (Ubuntu, t3.micro) and runs as a systemd service — starts on boot, restarts on crash.

```bash
# SSH into server
ssh -i "VCloud.pem" ubuntu@<EC2_PUBLIC_IP>

# Rebuild and restart after code changes
mvn clean package -DskipTests
sudo systemctl restart vercelclone
sudo systemctl status vercelclone
```

---

## Screenshots

**Login Page**

<img width="1835" height="935" alt="Screenshot 2026-06-09 095536" src="https://github.com/user-attachments/assets/12250d3d-af99-4d14-94a6-50a2f2fa7854" />


**Dashboard**

<img width="1913" height="911" alt="Screenshot 2026-06-09 095620" src="https://github.com/user-attachments/assets/ee30c3a9-1800-4342-a6a6-ae5b6a9a6c09" />


**Deploy Form**

<img width="898" height="799" alt="Screenshot 2026-06-09 095641" src="https://github.com/user-attachments/assets/07c69b48-ee44-4ea4-95ed-48829188aa36" />

---

## What I Learned

- Building a production-grade REST API with Spring Boot from scratch
- Implementing JWT authentication with Spring Security filter chain
- Running external processes (git, docker) from Java using ProcessBuilder
- Async job queue architecture with LinkedBlockingQueue
- AWS S3 file upload using SDK v2
- Deploying a Spring Boot application on EC2 as a systemd service
- Secure credential management — rotating AWS keys, using .gitignore

---

## Author

**Manish Kumar**
B.Tech Computer Science, Quantum University, Roorkee
[GitHub](https://github.com/maniking1299) | [LinkedIn](https://www.linkedin.com/in/manishkumar1299/)

---

> Built as a portfolio project to demonstrate Java backend development skills including REST APIs, security, async processing, and cloud deployment.
