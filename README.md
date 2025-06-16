# 🚀 RoomieMatcher – AI-Powered Roommate Matching System

RoomieMatcher is a web application that helps users find compatible roommates based on their preferences and lifestyle choices. The application uses an intelligent matching algorithm to calculate compatibility scores between potential roommates.

## 🌟 Features

- **User Authentication**: Secure registration and login system
- **Preference Setting**: Users can set their preferences for budget, location, cleanliness, noise tolerance, smoking, and pets
- **AI-Powered Matching**: Smart algorithm that calculates compatibility scores based on user preferences
- **Match Visualization**: Clear display of potential roommates with compatibility scores
- **Responsive Design**: Works on desktop and mobile devices

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
   spring.datasource.username=your_username
   spring.datasource.password=your_password
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
   http://localhost:8081
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