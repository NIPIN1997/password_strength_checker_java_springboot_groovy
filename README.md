# Password Rule Engine Application (Spring Boot + Groovy + Docker)

## Overview
This project is a **dynamic password validation service** built using Spring Boot. It evaluates password strength based on external groovy rules, allowing to **add or modify password rules without redeploying the application**. The application is containerized with **Docker**, making it easy to run anywhere without setting up Java/Groovy locally.

## Features
- REST API for password strength checks
- Dynamic scripting using GroovyShell
- Unit tests with Spock Framework
- Swagger UI for API documentation
- Dockerized with volume mounting for external scripts

## Build & Run
Navigate to password-rule-engine folder
```bash
cd password-rule-engine
```
### 1. Build the Jar File
```bash
./gradlew bootJar
```
### 2. Build Docker Image
```bash 
docker build -t password-rule-engine .
```
### 3. Run Docker Container
```bash
docker run --name password-rule-engine -p 8080:8080 -d -v ../scripts:/app/scripts password-rule-engine
```

## API Endpoint
### Swagger UI
Access the Swagger UI for full API documentation at:<br>
`http://localhost:8080/swagger-ui/index.html`

## Check Password Strength
### POST /check-password-strength
Example Request(cURL)<br>
`curl --location --request POST 'http://localhost:8080/check-password-strength?password=Abcd1234'`

Example Response(Strong Password)<br>
```json
{
    "status": "SUCCESS",
    "message": "Password checked successfully.",
    "passwordStrong": true,
    "time": "12-03-2026 10:03:30"
}
```

Example Response(Weak Password)<br>
```json
{
    "status": "SUCCESS",
    "message": "Password checked successfully.",
    "passwordStrong": false,
    "data": [
        "Password should contain a minimum of 8 characters.",
        "Password should contain at least one uppercase character."
    ],
    "time": "12-03-2026 10:06:16"
}
```

## Groovy Rules Example
```groovy
enabled = true
if (password =~ /[A-Z]/) {
    passed = true
} else {
    passed = false
    message = "Password should contain at least one uppercase character."
}
```
