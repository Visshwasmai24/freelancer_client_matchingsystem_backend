# Freelancer-Client Matching System — Backend

A Spring Boot REST API backend for the Freelancer-Client Matching Platform. Handles authentication, skill-based matching, proposals, messaging, resume parsing, and an AI chatbot.

🔗 **Live API:** [freelancer-client-matchingsystem-backend on Render](https://freelancer-client-matchingsystem-backend.onrender.com)
🔗 **Frontend:** [freelancer-client-matchingsystem-fr.vercel.app](https://freelancer-client-matchingsystem-fr.vercel.app)

---

## Features

### Authentication
- JWT-based login and registration
- Role-based access control (FREELANCER / CLIENT / ADMIN)
- BCrypt password encryption

### Skill Matching Algorithm
- Freelancer side: projects ranked by % skill overlap with freelancer's skills
- Client side: freelancers ranked by % skill overlap per project
- Returns matched skills highlighted for each result

### Resume Parsing
- Upload PDF resume
- Automatically extracts skills using Apache PDFBox text extraction
- Matches extracted text against a dictionary of 80+ tech skills
- Updates freelancer profile with extracted skills

### AI Chatbot
- Powered by HuggingFace Mistral-7B model
- Answers questions about the platform
- Rule-based fallback responses if AI is unavailable

### Proposals
- Freelancers submit proposals with bid amount and description
- Clients accept or reject proposals
- Status tracking (PENDING / ACCEPTED / REJECTED)

### Messaging
- Real-time inbox between clients and freelancers
- Conversation history per user pair

### Reviews
- Clients rate freelancers after project completion (1-5 stars)
- Average rating displayed on freelancer profile

### Admin
- Manage users: block / unblock / delete
- Manage projects: approve / reject
- Platform-wide statistics and reports

---

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Spring Boot 3.2.5 | Backend framework |
| Spring Security | Authentication & authorization |
| JWT (jjwt) | Token-based auth |
| Spring Data JPA | Database ORM |
| PostgreSQL | Production database |
| HikariCP | Connection pooling |
| Apache PDFBox | PDF text extraction |
| WebFlux WebClient | HuggingFace API calls |
| Docker | Containerization |

---

## API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Projects
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/projects` | Get all projects |
| GET | `/api/projects/client/{id}` | Get projects by client |
| POST | `/api/projects` | Post new project |

### Matching
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/match/freelancer/{id}` | Get matched projects for freelancer |
| GET | `/api/match/project/{id}` | Get matched freelancers for project |

### Proposals
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/proposals` | Submit proposal |
| GET | `/api/proposals/freelancer/{id}` | Get freelancer's proposals |
| GET | `/api/proposals/project/{id}` | Get proposals for a project |
| PUT | `/api/proposals/{id}/accept` | Accept proposal |
| PUT | `/api/proposals/{id}/reject` | Reject proposal |

### Messages
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/messages` | Send message |
| GET | `/api/messages/inbox/{id}` | Get inbox |
| GET | `/api/messages/conversation` | Get conversation between two users |

### Reviews
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/reviews` | Submit review |
| GET | `/api/reviews/freelancer/{id}` | Get reviews for freelancer |

### Resume
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/resume/upload` | Upload PDF and extract skills |

### Chatbot
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/chatbot/ask` | Send message to AI chatbot |

### Admin
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/freelancers` | Get all freelancers |
| GET | `/api/admin/clients` | Get all clients |
| GET | `/api/admin/projects` | Get all projects |
| GET | `/api/admin/reports` | Get platform stats |
| PUT | `/api/admin/users/{id}/block` | Block user |
| PUT | `/api/admin/users/{id}/approve` | Unblock user |
| DELETE | `/api/admin/users/{id}` | Delete user |

---

## Getting Started Locally

### Prerequisites
- Java 21
- Maven
- MySQL running locally

### Setup

```bash
# Clone the repo
git clone https://github.com/Visshwasmai24/freelancer_client_matchingsystem_backend.git
cd freelancer_client_matchingsystem_backend

# Create the database in MySQL
# Run this in MySQL Workbench or terminal:
# CREATE DATABASE skillmatch;

# Update application.properties for local MySQL:
# spring.datasource.url=jdbc:mysql://localhost:3306/skillmatch?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
# spring.datasource.username=root
# spring.datasource.password=your_password
# spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect

# Run the application
mvn spring-boot:run
```

API will be available at `http://localhost:8080`

---

## Environment Variables (Render Deployment)

| Variable | Description |
|----------|-------------|
| `DATABASE_URL` | PostgreSQL JDBC URL |
| `DB_USER` | Database username |
| `DB_PASS` | Database password |
| `HF_API_KEY` | HuggingFace API token for chatbot |
| `PORT` | Server port (set automatically by Render) |

---

## Deployment

Deployed on **Render** using Docker. Auto-deploys on every push to `main`.

Make sure to set all environment variables in Render's dashboard before deploying.

---

## Related

- [Frontend Repository](https://github.com/Visshwasmai24/freelancer_client_matchingsystem_frontend)
