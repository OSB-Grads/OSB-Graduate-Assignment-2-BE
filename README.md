# OSB-Graduate-Assignment-2-Backend

## Overview :

This is the backend service for **OSB-Graduate_assignment-2**, built using **Java (Spring Boot framework)**.
It provides REST APIs , buisness logic and data persistance.

## Techstack :

- **Framework**: Java,Spring Boot
- **Build Tool**: Maven (pom.xml)
- **Database**: PostgreSQL
- **Authentication**: JWT
- **Libraries**: Lombok , SpringData JPA etc
- **Deployment**: Docker , Kubernetes
- **Cloud**: MicroSoft Azure

## Prequisites :

- Java JDK 21
- Maven-3.x

## Getting Started :

### 1. Clone the repository

```bash
 git clone https://github.com/OSB-Grads/OSB-Graduate-Assignment-2-BE.git
 cd OSB-Graduate-Assignment-2-BE
```

### 2. Build the project

 ```bash
 mvn clean install 
 ```

### 3. Configure Environment

Create or update
"src/main/resources/application.properties" with your environment variables:

```bash
spring.application.name=webApplication
spring.datasource.url=jdbc:sqlite:Bank.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true
spring.jwt.secretkey=''
````

### 4. Run the application

```bash
mvn spring-boot:run
```

The application will be available at http://localhost:8080

## API Endpoints :

### 1. Authentication Endpoints

- **/api/v1/auth/register** -> register user and generate JWT token
- **/api/v1/auth/login** -> user login
- **/api/v1/auth/refreshtoken** -> generate a new JWT token for the authenticated user using refresh token
- **/api/v1/auth/logout** -> user logout

### 2. User Endpoints

- **api/v1/users/me** -> PUT,GET,PATCH for create-user, get-user-details and update-user respectively

### 3. Account Endpoints

- **/api/v1/accounts** -> POST,GET for create-account,get-all-accounts
- **/api/v1/accounts/{accountNumber}** -> for get-account-by-accountnumber

### 4. Transaction Endpoints

- **api/v1/transactions/deposit** -> deposit
- **api/v1/transactions/withdraw** -> withdraw
- **api/v1/transactions/transfer** -> transfer
- **api/v1/transactions/{accountNumber}** -> get-Transaction-History
- **api/v1/transactions/{accountNumber}** -> get-TransactionHistory-By-UserId (GET)

### 5. Product Endpoints

- **/api/v1/product/fetch/{productId}** -> fetch-Product-Details-By-Id
- **/api/v1/product/fetch** -> fetch-Product-Details
- **/api/v1/product/create** -> create-Product(only with ADMIN Role)
- **/api/v1/product/update/{productId}** -> Update-Product with product id(only with ADMIN Role)
- **/api/v1/product/delete/{productId}** -> Delete-Product with product id(only with ADMIN Role)

### 6. Log Endpoints

- **/api/v1/logs** -> retrieve-all-Logs
- **/api/v1/logs/{userid}** -> retrieve-Logs-By-UserId
- **/api/v1/logs//logId/{logId}**  -> retrieve-Logs-By-LogId

### 7.ForgotPassword Endpoints

- **api/v1/forgotPassword/{email}** -> Verify the email exists in the dataBase
- **api/v1/forgotPassword/resendOtp/{email}** -> Resend OTP
- **api/v1/forgotPassword/verify-otp** -> Verify OTP
- **api/v1/forgotPassword/resetPassword** -> Reset Password

### 8.Admin Endpoints

- **/api/v1/admin/user** -> get All Users
- **/api/v1/admin/accounts** -> get All Accounts

## Testing :

Run unit tests :

```bash
mvn test
```

## PROJECT STRUCTURE :

```bash
OSB-Graduate-Assignment-2-BE
        ├── src                   
        │          ├── main
        │          │  ├── java                       # Source code
        │          │  │  ├── auth/                   # Authentication business logic
        │          │  │   ├── user/                  # Customer profile management
        │          │  │   ├── account/               # Bank account management
        │          │  │   ├── transaction/           # Banking transaction logic
        │          │  │   ├── logging/               # Logging logic
        │          │  │   ├── db/                    # Database interaction layer
        │          │  │   │   ├── auth/              # DB access for auth data
        │          │  │   │   ├── user/              # DB access for user data
        │          │  │   │   ├── account/           # DB access for account data
        │          │  │   │   ├── transaction/       # DB access for transaction data
        │          │  │   │   └── logging/           # DB access for logs
        │          │  │   ├── dto/                   # Data Transfer Objects
        │          │  │   ├── mapper/                # Entity ↔ DTO mappers
        │          │  │   ├── orchestrator/          # Service coordinators
        │          │  │   ├── controller/            # End points
        │          │  │   └── exception/             # Custom exception classes
        │          │  │
        │          │  └── resources                  # Config files (application.properties)
        │          │
        │          └── test                          # Unit tests
        │
        ├── pom.xml                                  # Maven configuration
        └── README.md                                # Project documentation
```