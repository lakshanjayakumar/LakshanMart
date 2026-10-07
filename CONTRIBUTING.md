# Contributing to LakshanMart

Thank you for contributing to **LakshanMart**. Please follow these workflow conventions and guidelines aligned with the Anna University R2025 Semester 3 Capstone Specification.

---

## 1. Local Development Setup

Follow these exact steps from `git clone` to a running local instance:

### Step 1: Clone the repository
```bash
git clone https://github.com/lakshan/LakshanMart.git
cd LakshanMart
```

### Step 2: Configure Environment
Copy `.env.example` to `.env` and adjust configuration properties if needed:
```bash
cp .env.example .env
```

### Step 3: Build the Project
Ensure JDK 17 is active in your environment, then execute:
```bash
mvn clean package
```
Verify that the build completes successfully and produces `target/LakshanMart.war`.

### Step 4: Run Automated Tests
```bash
mvn test
```

### Step 5: Run on Apache Tomcat 9.0.x
1. Download and extract [Apache Tomcat 9.0.x](https://tomcat.apache.org/download-90.cgi).
2. Copy `target/LakshanMart.war` to `$CATALINA_HOME/webapps/LakshanMart.war`.
3. Start Tomcat:
   - On Linux/macOS: `$CATALINA_HOME/bin/startup.sh`
   - On Windows: `%CATALINA_HOME%\bin\startup.bat`
4. Open your browser and navigate to:
   `http://localhost:8080/LakshanMart/`

---

## 2. Git Branching & Commit Conventions

### Branch Model
- `main`: Protected and always deployable.
- `feature/<feature-name>`: Dedicated branch per feature (e.g., `feature/user-auth`, `feature/cart-dao`).
- Merge back to `main` via reviewed Pull Request only when CI tests pass.

### Commit Message Format
Strict conventional commits format:
- `feat: <description>` - New business capability
- `fix: <description>` - Bug fix
- `test: <description>` - Adding or refactoring tests
- `docs: <description>` - Documentation changes

---

## 3. Definition of Done (DoD) Checklist

Before submitting a Pull Request:
- [ ] Code compiles without warnings or errors.
- [ ] Unit and DAO tests written and passing (`mvn clean verify`).
- [ ] PreparedStatement used for all database interactions (no string concatenation).
- [ ] Layered architecture boundaries maintained (no JDBC in service, no business logic in servlets).
- [ ] Documentation updated where applicable.
