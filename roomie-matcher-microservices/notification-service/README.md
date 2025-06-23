# Notification Service

This microservice is responsible for sending email notifications in the RoomieMatcher application.

## Features

- Send emails using templates
- Support for verification codes, password resets, and notifications
- Configurable email providers (Standard Java Mail or AWS SES)
- HTML email templates with Thymeleaf

## API Endpoints

### Email Notifications

- `POST /notifications/email` - Send an email notification

## Architecture

- Uses Spring Boot and Spring Mail
- Templating with Thymeleaf
- AWS SES integration for production
- Standard Java Mail for development

## Email Templates

The service includes HTML templates for:

1. Account verification emails
2. Password reset emails
3. Match notification emails

## Configuration

### Application Properties

```yaml
server:
  port: 8084
  servlet:
    context-path: /api/v1

spring:
  application:
    name: notification-service
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
```

### Environment Variables

For production deployment:

- `MAIL_HOST` - SMTP server hostname
- `MAIL_PORT` - SMTP server port
- `MAIL_USERNAME` - SMTP username
- `MAIL_PASSWORD` - SMTP password
- `NOTIFICATION_PROVIDER` - Email provider (standard or aws-ses)
- `AWS_SES_ACCESS_KEY` - AWS access key for SES
- `AWS_SES_SECRET_KEY` - AWS secret key for SES
- `AWS_SES_REGION` - AWS region for SES
- `AWS_SES_FROM_EMAIL` - From email address for SES

## Docker

The service includes a Dockerfile for containerized deployment:

```dockerfile
FROM maven:3.8.6-openjdk-17-slim AS builder
# ... build steps ...

FROM eclipse-temurin:17.0.5_8-jre-alpine
# ... runtime configuration ...
```

## Local Development

For local development, the Docker Compose configuration includes a MailDev server that captures emails for testing purposes. Access the MailDev web interface at http://localhost:1080. 