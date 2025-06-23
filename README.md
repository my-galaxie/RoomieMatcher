# RoomieMatcher Microservices

RoomieMatcher is a platform that helps users find compatible roommates based on preferences like budget, location, cleanliness, noise tolerance, and more.

This project is a microservices implementation of the RoomieMatcher application, transformed from a monolithic Spring Boot application.

## Project Structure Cleanup

The project structure has been consolidated to use only the root directory structure, eliminating the duplicate `roomie-matcher-microservices` directory. This cleanup ensures:

- Single source of truth for all code
- Consistent dependency management
- Simplified build process
- Elimination of code duplication

To complete the cleanup, run the provided script:
```bash
chmod +x cleanup-duplicates.sh
./cleanup-duplicates.sh -y
```

## Architecture

The application is divided into the following microservices:

1. **Auth Service**: Handles user registration, authentication, and account verification
2. **Profile Service**: Manages tenant profiles and preferences
3. **Match Service**: Implements the roommate matching algorithm
4. **Notification Service**: Sends email notifications
5. **API Gateway**: Routes requests to appropriate services

## Technology Stack

- **Java 17**: Programming language
- **Spring Boot 3.2.0**: Application framework
- **Spring Cloud**: Microservices ecosystem
- **Spring Security**: Authentication and authorization
- **PostgreSQL**: Database
- **Flyway**: Database migrations
- **Docker**: Containerization
- **AWS**: Deployment platform

## Local Development

### Prerequisites

- Java 17
- Maven
- Docker and Docker Compose
- Git

### Running Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/roomiematcher.git
   cd roomiematcher
   ```

2. Start the services using Docker Compose:
   ```bash
   docker-compose up -d
   ```

3. The services will be available at:
   - API Gateway: http://localhost:8080
   - Auth Service: http://localhost:8081
   - Profile Service: http://localhost:8082
   - Match Service: http://localhost:8083
   - Notification Service: http://localhost:8084

### Development Workflow

1. Make changes to a service
2. Build the service:
   ```bash
   cd <service-directory>
   mvn clean package
   ```
3. Rebuild the Docker container:
   ```bash
   docker-compose build <service-name>
   docker-compose up -d <service-name>
   ```

## API Documentation

Each service has its own Swagger UI documentation available at `/swagger-ui.html` endpoint.

- API Gateway: http://localhost:8080/swagger-ui.html
- Auth Service: http://localhost:8081/swagger-ui.html
- Profile Service: http://localhost:8082/swagger-ui.html
- Match Service: http://localhost:8083/swagger-ui.html
- Notification Service: http://localhost:8084/swagger-ui.html

## AWS Elastic Beanstalk Deployment

### Prerequisites

- AWS Account
- AWS CLI installed and configured
- GitHub account (for GitHub Actions deployment)

### Deployment Options

#### Option 1: Using GitHub Actions

1. Set up the required GitHub secrets as described in [DEPLOYMENT_SECRETS.md](DEPLOYMENT_SECRETS.md)
2. Push your changes to the main branch
3. GitHub Actions will automatically deploy to AWS Elastic Beanstalk

#### Option 2: Manual Deployment

1. Use the provided deployment script:
   ```bash
   chmod +x deploy-to-beanstalk.sh
   ./deploy-to-beanstalk.sh --bucket your-s3-bucket-name --region your-aws-region
   ```

2. Monitor the deployment in the AWS Elastic Beanstalk console

### Deployment Documentation

For detailed deployment instructions, refer to:

- [AWS Beanstalk Deployment Guide](aws-beanstalk-deployment-guide.md): Comprehensive guide with step-by-step instructions
- [AWS Beanstalk Quick Start](aws-beanstalk-quickstart.md): Quick start guide for rapid deployment
- [AWS Beanstalk Cost Optimization](aws-beanstalk-cost-optimization.md): Tips for optimizing costs

## Security Considerations

- All sensitive information (JWT secrets, database credentials, AWS keys) should be stored as environment variables or AWS secrets
- Do not commit any sensitive information to the repository
- Use IAM roles with least privilege principle
- Enable HTTPS for all communications

## Project Structure

```
roomiematcher/
├── api-gateway-service/      # API Gateway microservice
├── auth-service/             # Authentication microservice
├── common-lib/               # Shared library with DTOs and utilities
├── deployment/               # Deployment scripts and CloudFormation templates
├── docker-compose.yml        # Local development setup
├── init-db/                  # Database initialization scripts
├── match-service/            # Match algorithm microservice
├── notification-service/     # Email notification microservice
├── pom.xml                   # Parent Maven POM file
└── profile-service/          # User profile microservice
```

## Features

- User registration and authentication with email verification
- Tenant profile creation and management
- Roommate matching based on compatibility factors
- Email notifications for account verification and matches
- JWT-based authentication
- API gateway with rate limiting and circuit breaking

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -am 'Add new feature'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🚀 RoomieMatcher – Intelligent Roommate Matching System

RoomieMatcher is a web application that helps users find compatible roommates based on their preferences and lifestyle choices. The application uses an intelligent matching algorithm to calculate compatibility scores between potential roommates.

## 🌟 Features

- **User Authentication**: Secure registration and login system
- **Preference Setting**: Users can set their preferences for budget, location, cleanliness, noise tolerance, smoking, and pets
- **Smart Matching Algorithm**: Smart algorithm that calculates compatibility scores based on user preferences
- **Match Visualization**: Clear display of potential roommates with compatibility scores
- **Responsive Design**: Works on desktop and mobile devices
- **Email Verification**: OTP-based email verification
- **Profile Management**: Manage user profile and preferences
- **Search and Filter**: Search and filter potential roommates
- **Gender-Based Filtering**: Filter roommates based on gender
- REST API endpoints
- Styled web interface using Thymeleaf + Bootstrap
- PostgreSQL database integration
- Java OOP concepts: Inheritance, Interfaces, Threads implemented

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

This project is created for learning purposes only.

## 👥 Creator

Sahana H J 

## Development Notes

- Email functionality is configured in development mode by default
- Sample OTPs are printed to the console for testing
- To enable actual email sending, update the email configuration in `application.properties` 

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

## 📬 Contact
For support or suggestions, open an issue ,please  contact sahanahj5711@gmail.com

