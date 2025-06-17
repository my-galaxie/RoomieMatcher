<<<<<<< HEAD
# 🚀 RoomieMatcher – AI-Powered Roommate Matching System

RoomieMatcher is a web application that helps users find compatible roommates based on their preferences and lifestyle choices. The application uses an intelligent matching algorithm to calculate compatibility scores between potential roommates.

## 🌟 Features

- **User Authentication**: Secure registration and login system
- **Preference Setting**: Users can set their preferences for budget, location, cleanliness, noise tolerance, smoking, and pets
- **AI-Powered Matching**: Smart algorithm that calculates compatibility scores based on user preferences
- **Match Visualization**: Clear display of potential roommates with compatibility scores
- **Responsive Design**: Works on desktop and mobile devices
- **Email Verification**: OTP-based email verification
- **Profile Management**: Manage user profile and preferences
- **Search and Filter**: Search and filter potential roommates
- **Gender-Based Filtering**: Filter roommates based on gender

## 🛠️ Technology Stack

- **Backend**: Java with Spring Boot
- **Frontend**: Thymeleaf templates with Bootstrap 5
- **Database**: PostgreSQL
- **Security**: Spring Security with BCrypt password encoding
- **ORM**: Hibernate/JPA

## 📋 Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Maven (or use the included Maven wrapper)

## 🚀 Getting Started

### Database Setup

1. Create a PostgreSQL database named `roomiematcher_db`:
   ```sql
   CREATE DATABASE roomiematcher_db;
   ```

2. Update the `application.properties` file with your database credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/roomiematcher_db
   spring.datasource.username=postgres
   spring.datasource.password=12345
   ```

### Running the Application

1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/roomiematcher.git
   cd roomiematcher
   ```

2. Run the application using Maven:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Access the application in your browser:
   ```
   http://localhost:8080
   ```

### Data Persistence

The application is configured to maintain user data and preferences between restarts. The following settings in `application.properties` ensure data persistence:

```
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=never
```

### Initializing Sample Data

To initialize the database with sample data (only needed once):

1. Set `app.init-data=true` in `application.properties`
2. Run the application
3. After the data is initialized, set `app.init-data=false` to prevent re-initialization

Alternatively, you can run the `init-data.sql` script directly in your PostgreSQL database:

```
psql -U postgres -d roomiematcher_db -f src/main/resources/init-data.sql
```

## 🧮 Matching Algorithm

The matching algorithm calculates compatibility scores based on several factors:

- **Budget Compatibility**: Higher score for similar budget ranges
- **Location Preference**: Maximum points if both users prefer the same location
- **Lifestyle Compatibility**:
  - Cleanliness level (1-5 scale)
  - Noise tolerance (1-5 scale)
  - Smoking preference (yes/no)
  - Pet preference (yes/no)

The algorithm assigns weights to each factor and calculates a final score out of 100.

## 📱 Application Flow

1. **Registration**: New users create an account
2. **Preferences**: Users set their roommate preferences
3. **Matching**: The system calculates compatibility scores with other users
4. **Results**: Users can view and contact potential roommates

## 🔒 Security

- Passwords are securely hashed using BCrypt
- Form validation to prevent common security issues
- CSRF protection enabled

## 🔄 Future Enhancements

- Chat functionality between matched users
- Profile pictures and more detailed user profiles
- Advanced filtering options
- Mobile app version
- Integration with map services for location-based matching

## 📜 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Contributors

- Your Name - Initial work 

## Development Notes

- Email functionality is configured in development mode by default
- Sample OTPs are printed to the console for testing
- To enable actual email sending, update the email configuration in `application.properties` 
=======
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
---

##  License
This project is for educational use.

---

## 📬 Contact
For support or suggestions, open an issue or contact sahanahj5711@gmail.com

>>>>>>> 8a01580d059cc8abb6926a2c165f19c967d8181d
