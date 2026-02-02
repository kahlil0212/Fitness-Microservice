# Fitness Microservice

A comprehensive microservices-based fitness tracking application built with Spring Boot and modern web technologies. This system provides activity tracking, user management, and AI-powered fitness insights through a distributed architecture.

## 🏗️ Architecture

This project follows a microservices architecture pattern with the following components:

### Core Services

- **User Service** - Manages user registration, authentication, and profile information
- **Activity Service** - Handles workout logging, activity tracking, and fitness data management
- **AI Service** - Provides intelligent fitness recommendations and insights using AI/ML algorithms

### Infrastructure Services

- **Config Server** - Centralized configuration management for all microservices
- **Eureka Server** - Service discovery and registration
- **Gateway** - API Gateway for routing and load balancing
- **Frontend** - React/JavaScript-based user interface

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Node.js 14+ and npm (for frontend)
- Docker (optional, for containerized deployment)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/kahlil0212/Fitness-Microservice.git
   cd Fitness-Microservice
   ```

2. **Start the Config Server first**
   ```bash
   cd configserver
   mvn spring-boot:run
   ```

3. **Start the Eureka Server**
   ```bash
   cd eureka
   mvn spring-boot:run
   ```

4. **Start the microservices**
   
   In separate terminals:
   ```bash
   # User Service
   cd user-service
   mvn spring-boot:run
   
   # Activity Service
   cd activityservice
   mvn spring-boot:run
   
   # AI Service
   cd aiservice
   mvn spring-boot:run
   ```

5. **Start the API Gateway**
   ```bash
   cd gateway
   mvn spring-boot:run
   ```

6. **Start the Frontend**
   ```bash
   cd fitness-app-frontend
   npm install
   npm start
   ```

## 🔧 Configuration

The Config Server manages centralized configuration for all services. Configuration files should be placed in the config repository referenced by the Config Server.

### Default Ports

- Config Server: `8888`
- Eureka Server: `8761`
- Gateway: `8080`
- User Service: `8081`
- Activity Service: `8082`
- AI Service: `8083`
- Frontend: `5173`

## 📡 API Endpoints

Access all services through the API Gateway at `http://localhost:8080`

### User Service
- `POST /api/users/register` - Register new user
- `GET /api/users/{id}` - Get user profile
- `GET /api/users/{id}/validate` - Validate user 

### Activity Service
- `POST /api/activities/addActivity` - Log new activity
- `GET /api/activities/getUserActivities` - Get user activities
- `GET /api/activities/{id}` - Get specific activity

### AI Service
- `POST /api/recommendations/{userId}` - Get User's recommendations
- `GET /api/recommendations/{activityId}` - Get Activity receommendations

## 🛠️ Technology Stack

### Backend
- **Java 17** - Primary programming language
- **Spring Boot** - Application framework
- **Spring Cloud** - Microservices framework
  - Spring Cloud Config
  - Spring Cloud Netflix Eureka
  - Spring Cloud Gateway
- **Maven** - Dependency management

### Frontend
- **JavaScript/React** - UI framework
- **CSS** - Styling
- **HTML** - Markup

## 📊 Service Discovery

The Eureka Server dashboard is available at `http://localhost:8761` where you can monitor the health and status of all registered services.

## 🔐 Security

- OAuth2 with PKCE
- API Gateway for centralized security
- Service-to-service communication through Eureka

## 🐳 Docker Deployment (Future enhancement)

(Add docker-compose.yml if available)

```bash
docker-compose up -d
```

## 👤 Author

**Kahlil**
- GitHub: [@kahlil0212](https://github.com/kahlil0212)

## 🙏 Acknowledgments

- Spring Cloud documentation
- Netflix OSS
- React community


**Note**: Make sure to start services in the correct order (Config Server → Eureka → Services → Gateway → Frontend) to ensure proper service registration and discovery.
