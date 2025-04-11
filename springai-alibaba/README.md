# Spring AI Alibaba Demo

This module demonstrates the integration of Spring AI with Alibaba Cloud services to build agentic applications.

## Features

- Spring AI integration
- RESTful API endpoints
- Actuator endpoints for monitoring
- Integration with Alibaba Cloud services

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Spring Boot 3.4.4
- Spring AI 1.0.0-M6

## Getting Started

1. Build the project:
   ```bash
   mvn clean install
   ```

2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

3. Access the application:
   - Application: http://localhost:8080
   - Health Check: http://localhost:8080/actuator/health
   - Info: http://localhost:8080/actuator/info

## Project Structure

```
springai-alibaba/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/feng/springai/alibaba/
│   │   │       └── SpringAiAlibabaApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/
└── pom.xml
``` 