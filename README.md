# Wallet Services

## Description

This project is a REST API service for managing digital wallets. It allows the creation of wallets, checking current and historical balances, depositing, withdrawing, and transferring funds between wallets.

## Features

- **Create Wallet**: Allows the creation of wallets for users.
- **Check Balance**: Retrieves the current balance of a user's wallet.
- **Check Historical Balance**: Retrieves the balance of a user's wallet at a specific point in the past.
- **Deposit Funds**: Enables users to deposit money into their wallets.
- **Withdraw Funds**: Enables users to withdraw money from their wallets.
- **Transfer Funds**: Facilitates the transfer of money between user wallets.

## Technologies Used

- **Java**: Main programming language.
- **Spring Boot**: Framework for building Java applications.
- **Maven**: Dependency management and build tool.
- **H2 Database**: In-memory database for testing.
- **MapStruct**: Framework for object mapping.
- **Lombok**: Library to reduce boilerplate code.

## The project organization of packages

The organization of packages is crucial for maintaining a clean separation of concerns and ensuring that the application is modular and maintainable. Here is an explanation of the package organization.
I try to use the Domain-Driven Design (DDD) and Hexagonal Architecture for this organization.

### 1. Domain Layer
This layer contains the core, use cases and domain entities. It is independent of any external frameworks or technologies.

- **`domain.model`**: Contains the domain entities and value objects.
- **`domain.usecases`**: Contains domain business use cases.

### 2. Application Layer
This layer contains the application services that orchestrate the use cases of the application. It coordinates between the domain layer and the infrastructure layer.

- **`application.services`**: Contains application services that implement the use cases of the application.
- **`application.dto`**: Data Transfer Objects (DTOs) used for communication between layers.

### 3. Adapters Layer
This layer contains the adapters that interact with external systems, such as web controllers, database repositories, and other external services.

- **`adapters.web.controllers`**: Contains the REST controllers that handle HTTP requests and responses.
- **`adapters.web.dto`**: DTOs specific to the web layer.
- **`adapters.persistence`**: Contains the implementations of the repository interfaces defined in the domain layer.

### 4. Configuration Layer
This layer contains the configuration classes for setting up the application, such as Spring configuration classes.

- **`config`**: Contains configuration classes for setting up the application context, security, and other configurations.

### Package Structure
```
src/main/java/com/example/walletservices
├── adapters
│   ├── persistence
│   └── web
│       ├── controllers
│       └── dto
├── application
│   ├── port
│   └── services
├── config
└── domain
    ├── model
    └── usecases
```

This organization ensures that the core business logic (domain layer) is isolated from the infrastructure and application concerns, promoting a clean and maintainable architecture.


## Endpoints

### Wallets

- **GET /wallets/{walletId}/balance**: Get the balance of the wallet.
    - Response: `200 OK`
    - Response body: `{"balance": "decimal"}`

- **POST /wallets**: Creates a new wallet.
    - Request Body: `{"userId": "string", "name": "string"}`
    - Response: `201 Created`

### Transactions

- **POST /wallets-transactions/deposit**: Deposits funds into a wallet.
    - Request Body: `{"walletId": "long", "amount": "decimal"}`
    - Response: `201 Created`

- **POST /wallets-transactions/withdraw**: Withdraws funds from a wallet.
    - Request Body: `{"walletId": "long", "amount": "decimal"}`
    - Response: `201 Created`

- **POST /wallets-transactions/transfer**: Transfers funds between wallets.
    - Request Body: `{"fromWalletId": "long", "toWalletId": "long", "amount": "decimal"}`
    - Response: `201 Created`

## How to Start the Service

To start the Wallet Services application using Maven, follow these steps:

1. **Ensure you have Maven installed**: Make sure you have Apache Maven installed on your system. You can download it from the [official Maven website](https://maven.apache.org/download.cgi).

2. **Navigate to the project directory**: Open a terminal or command prompt and navigate to the root directory of the project where the `pom.xml` file is located.

3. **Build the project**: Run the following command to build the project and download all necessary dependencies:
   ```sh
   mvn clean install
   ```

4. **Run the application**: After the build is successful, you can start the application using the following command:
   ```sh
   # Restart a specific service
   docker compose up

   # Stop all services and remove containers, volumes
   docker compose down -v
   ```

5. **Access the application**: Once the application is running, you can access the API endpoints using a tool like Postman or a web browser. The default URL for the application will be:
   ```
   http://localhost:8080
   ```

## Documentation and Testing

- **Documentation**: The API documentation is available in the `docs` folder. You can find the Swagger file (`docs/wallet-api-swagger.yaml`) and Postman collection (`docs/Walllet-API.postman_collection.json`) for testing the API endpoints.
- **Testing**: Use the provided Postman collection to test the API endpoints. Import the collection into Postman and execute the requests to verify the functionality.

## For Production environment

### To avoid downtime will be necessary
To avoid downtime of your service, you can implement several strategies:

1. **Load Balancing (AWS LB)**: Distribute incoming traffic across multiple instances of your service to ensure no single instance is overwhelmed.
2. **Auto-Scaling (AWS ECS)**: Automatically adjust the number of running instances based on the current load. Deploy this in the cloud environment, using AWS ECS for example.
3. **Health Checks**: Regularly monitor the health of your service instances and automatically replace any that fail.
4. **Database Replication (AWS RDS)**: Use database replication to ensure high availability and failover capabilities. Use a managed database service like AWS RDS.
5. **Caching (Redis)**: Implement caching to reduce load on your service and improve response times.
6. **Monitoring and Alerts (APM)**: Set up monitoring and alerting to detect and respond to issues quickly. Use any APM tool like New Relic, Datadog, or AWS CloudWatch.

## For Security
It is important to consider security aspects when deploying a service to production:
1. **Oauth**: Implement OAuth for authentication and authorization. Use a service like AWS Cognito or Auth0 for user management.
2. **HTTPS**: Ensure that all communication with the service is encrypted using HTTPS.
3. **Input Validation**: Validate all input data to prevent injection attacks and other vulnerabilities.
4. **API Gateway (AWS API Gateway)**: Use an API Gateway to manage and secure your API endpoints and apply rate limiting, throttling, and other security features.


By combining these strategies, you can significantly reduce the risk of downtime and ensure high availability for your service.

## Conclusion

This project provides a simple implementation of a REST API for managing digital wallets. It includes basic functionalities such as creating wallets, checking balances, and performing transactions. However, it is important to note that this implementation does not consider aspects such as security, authorization, authentication, and some validations. These features are crucial for a production-ready application and should be implemented to ensure the safety and integrity of the service.