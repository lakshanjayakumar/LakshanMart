# LakshanMart - Multi-Seller E-Commerce Marketplace

[![build-and-test](https://github.com/lakshan/LakshanMart/actions/workflows/build.yml/badge.svg)](https://github.com/lakshan/LakshanMart/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17](https://img.shields.io/badge/Java-17%20LTS-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Servlet 4.0](https://img.shields.io/badge/Servlet-4.0%20(Tomcat%209.0.x)-blue.svg)](https://tomcat.apache.org/)

Multi-seller e-commerce marketplace web application developed in compliance with the **Anna University R2025 Semester 3 Capstone Project Specification**.

---

## 1. Problem Statement

Modern retail necessitates scalable, decentralized online commerce platforms that allow independent merchants to list and sell items while giving consumers a unified, secure purchasing journey. **LakshanMart** provides a clean, robust, and performant web platform featuring:

- Multi-role authorization (**Buyer**, **Seller**, **Admin**)
- Product catalog exploration, categorization, and full-text keyword search
- Persistent shopping cart and simulated mock checkout
- Complete order lifecycle management (Pending &rarr; Confirmed &rarr; Shipped &rarr; Delivered)
- Verified buyer reviews and star ratings
- Integrated AI conversational customer assistant powered by Gemini API (with deterministic fallback)

---

## 2. Technology Stack

| Layer / Component | Technology | Specification / Version |
| :--- | :--- | :--- |
| **Language & Runtime** | Java | JDK 17 (LTS) |
| **Servlet Container** | Apache Tomcat | 9.0.x (`javax.servlet.*` API 4.0.1) |
| **Build Tool** | Apache Maven | 3.9+ (`pom.xml`, WAR packaging) |
| **Database** | H2 Database | 2.2.x (Server mode deployed, embedded for dev/tests) |
| **Connection Pooling** | HikariCP | 5.1.0 (Managed via `ServletContextListener`) |
| **Presentation / Views** | JSP + JSTL | JSP 2.3, JSTL 1.2, Vanilla JavaScript + Fetch API |
| **JSON Serialization** | Google Gson | 2.10.x |
| **Password Hashing** | jBCrypt | 0.4 (Bcrypt cost factor 10) |
| **Logging** | SLF4J + Logback | SLF4J 2.0.x with MDC Request ID tracking |
| **Testing Framework** | JUnit 5 + Mockito | JUnit Jupiter 5.10.x, Mockito 5.11.x |
| **CI / CD** | GitHub Actions | Automated build, test, and package verification |

---

## 3. System Architecture

The application strictly implements a **Layered MVC Pattern** over Java Servlets:

```
Browser (HTML5 / CSS3 / Vanilla JS + Fetch)
          │
          ▼  HTTP Request
┌─────────────────────────────────────────────────────────────┐
│ Filter Layer                                                │
│  - EncodingFilter (UTF-8 character encoding)                │
│  - RequestLoggingFilter (MDC request-id generation)         │
│  - AuthFilter (Session & role verification)                 │
└─────────────────────────────┬───────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ Controller Layer (javax.servlet.http.HttpServlet)           │
│  - Thin HTTP orchestration only                             │
│  - No SQL, no direct business rules                         │
└─────────────────────────────┬───────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ Service Layer                                               │
│  - Pure business logic & validations                        │
│  - Depends on DAO interfaces, not concrete classes          │
│  - No JDBC code                                             │
└─────────────────────────────┬───────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ DAO Layer (Data Access Object)                              │
│  - Interfaces + JDBC implementations                        │
│  - Exclusive location of SQL queries                        │
│  - Strict PreparedStatement & try-with-resources only       │
└─────────────────────────────┬───────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ HikariCP Connection Pool (Owned by ServletContextListener)  │
└─────────────────────────────┬───────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ H2 Database Engine (Server mode / Embedded)                 │
└─────────────────────────────────────────────────────────────┘
```

### Java Package Structure

```
com.lakshan.lakshanmart
├── controller    # Servlets — thin, no SQL, no business logic
├── service       # Business rules, validation, orchestration
├── dao           # DAO interfaces and JDBC implementations (all SQL)
├── model         # Domain entities / POJOs
├── dto           # Request and response shapes for JSON/REST endpoints
├── filter        # Filters (AuthFilter, RequestLoggingFilter, EncodingFilter)
├── listener      # Application lifecycle (HikariCP init/teardown)
├── util          # Reusable helpers (PasswordUtil, ValidationUtil, JsonUtil)
└── exception     # Custom checked exceptions and error codes
```

---

## 4. Engineering Rules & Compliance

1. **PreparedStatements Only:** String-concatenated queries are strictly prohibited across the entire codebase.
2. **Password Security:** All passwords hashed with bcrypt (`jBCrypt`). Plaintext, MD5, and SHA1 storage are banned.
3. **Session Security:** Session management handled via `HttpSession`. Session IDs are regenerated upon login with an explicit 30-minute timeout.
4. **XSS Protection:** All user-supplied output is escaped prior to HTML rendering using `<c:out>` or `fn:escapeXml`.
5. **Connection Pool Management:** HikariCP connection pool lifecycle is exclusively managed by a single `ServletContextListener`. Direct `DriverManager.getConnection()` calls are prohibited.
6. **Resource Management:** Strict `try-with-resources` pattern applied to every `Connection`, `PreparedStatement`, and `ResultSet`.
7. **Structured Logging:** Every HTTP request receives an individual unique Request ID attached to the SLF4J MDC.

---

## 5. Getting Started & Setup Instructions

### Prerequisites
- **JDK 17** (Ensure `JAVA_HOME` is set to JDK 17)
- **Apache Maven 3.8+**
- **Apache Tomcat 9.0.x** (compatible with Java EE 8 / `javax.servlet` API 4.0)

### Clone & Configuration
```bash
git clone https://github.com/lakshan/LakshanMart.git
cd LakshanMart

# Copy environment configuration
cp .env.example .env
```

### Build & Package
```bash
# Clean and verify automated tests
mvn clean verify

# Package into WAR file
mvn clean package
```
The resulting deployable WAR file will be generated at `target/LakshanMart.war`.

### Local Deployment
Deploy `target/LakshanMart.war` into your Tomcat `webapps/` directory:
```bash
cp target/LakshanMart.war $CATALINA_HOME/webapps/
$CATALINA_HOME/bin/startup.sh   # (or startup.bat on Windows)
```
Access the application locally at:
`http://localhost:8080/LakshanMart/`

---

## 6. Project Roadmap & Checkpoints

- **Kickoff (Jul 24 – Jul 27):** Skeleton, Maven setup, and DB schema v1
- **Week 1 (Jul 27 – Aug 2):** Authentication, base DAO layer, local deployment
- **Week 2 (Aug 3 – Aug 9):** Core flow (browse, cart, mock order checkout)
- **MVP Review (Aug 10):** Working end-to-end user journey
- **Weeks 3–4 (Aug 10 – Aug 23):** Seller dashboard and admin moderation
- **Weeks 5–6 (Aug 24 – Sep 6):** Search/filter, order status workflow, reviews & ratings
- **Weeks 7–8 (Sep 7 – Sep 20):** Security hardening, automated test suites, cloud deploy
- **Weeks 9–10 (Sep 21 – Oct 4):** Gemini AI chatbot assistant integration
- **Final Review (Oct 10):** Comprehensive demo and final submission
