# RoomieMatcher - Roommate Compatibility Finder

RoomieMatcher is a Java-based Spring Boot web application that helps tenants find compatible roommates by calculating match scores based on preferences like location, budget, cleanliness, and noise tolerance.

---

##  Features
- Match score calculation using a custom algorithm
- Preloaded tenant data via `data.sql`
- REST API endpoints
- Styled web interface using Thymeleaf + Bootstrap
- PostgreSQL database integration
- Java OOP concepts: Inheritance, Interfaces, Threads

---

##  Technologies Used
- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Thymeleaf
- Bootstrap 5

---

##  Prerequisites
Install the following:
- Java JDK 17+
- Maven
- PostgreSQL
- Git

---

##  Clone the Repository
```bash
git clone https://github.com/<your-username>/roomiematcher.git
cd roomiematcher
```

---

##  Configure Database
1. Create a database in PostgreSQL:
```sql
CREATE DATABASE roomiematcher_db;
```

2. Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/roomiematcher_db
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true
```

---

## ▶️ Run the Application
### Option 1: With Maven Wrapper
```bash
./mvnw spring-boot:run
```
### Option 2: In VS Code / IntelliJ
Right-click on `RoomieMatcherApplication.java` and select **Run**.

---

## 🌐 Access the Web App
Visit:
```
http://localhost:8080/
```

### REST API Test:
```
http://localhost:8080/api/roommates/match/1/2
```

---

##  Sample Data
See `src/main/resources/data.sql` for preloaded users.
You can modify this to add more tenants.

---

## Java OOP Concepts Demonstrated
- **Inheritance**: `Tenant` inherits from `User`
- **Interfaces**: `MatchingAlgorithm` is implemented by `BasicMatching`
- **Thread**: Background data cleaning simulated in `RoommateService`
- **REST API**: To test end points
---

##  License
This project is for educational use.

---

## 📬 Contact
For support or suggestions, open an issue or contact sahanahj5711@gmail.com

