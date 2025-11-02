# 🔧 Indian Trade Mart - Backend API

**A comprehensive REST API server for the Indian Trade Mart e-commerce platform**

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Database](#database)
- [Authentication](#authentication)
- [Error Handling](#error-handling)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)
- [Support](#support)

---

## 🎯 Overview

Indian Trade Mart Backend is a Spring Boot REST API that powers the complete e-commerce and B2B marketplace platform. It handles all business logic, data management, authentication, payments, and integrations.

**API Base URL:** `https://indiantrademart-backend.onrender.com/api/v1`

### Key Capabilities

- 🔐 Multi-role authentication (Buyer, Vendor, Admin, Employee, Support, CTO)
- 🛍️ Complete e-commerce API (products, categories, cart, orders)
- 👥 User and vendor management
- 💳 Payment processing with Razorpay
- 📊 Advanced analytics and reporting
- 📂 Category and location hierarchy management
- 📁 File upload and storage
- 📧 Email notifications
- 🔄 Import/Export functionality
- 🌐 RESTful API design with comprehensive error handling
- 🔒 JWT token-based security

---

## ✨ Features

### 🔐 Authentication & Authorization
- User registration (buyer, vendor, admin)
- Email/phone login with OTP verification
- JWT token generation and refresh
- Password reset and recovery
- Role-based access control (RBAC)
- Multi-device session management

### 🛍️ Product Management
- Create, read, update, delete products
- Product categorization (Main → Sub → Micro)
- Inventory management
- Product search and filtering
- Product images and media
- Product approvals workflow

### 🛒 Shopping Cart & Orders
- Add/remove items from cart
- Cart persistence and sync
- Coupon and promotion application
- Order creation and management
- Order tracking and history
- Order status updates
- Order cancellation and returns

### 💳 Payment Processing
- Razorpay payment gateway integration
- Payment creation and verification
- Order payment tracking
- Refund processing
- Payment history

### 👥 Vendor Management
- Vendor registration and verification
- Vendor profile management
- Commission and revenue tracking
- Vendor sales analytics
- Vendor product approvals

### 🎛️ Admin Management
- User management (activate/deactivate)
- Vendor approval workflows
- Product approval system
- Category and location management
- System configuration
- Analytics and reporting dashboard
- Commission management

### 📊 Analytics & Reporting
- User acquisition metrics
- Product performance analytics
- Order and revenue analytics
- Vendor performance tracking
- Sales funnel analysis
- Custom report generation

### 📂 Data Management
- Hierarchical category management
- State and city location management
- Bulk import/export (CSV, Excel)
- Data backup and recovery
- Audit logging

---

## 🛠️ Tech Stack

### Backend Framework
- **Spring Boot 3.x** - Java framework for REST APIs
- **Spring Data JPA** - ORM for database operations
- **Spring Security** - Authentication and authorization
- **Spring Web** - REST controller support
- **Spring Mail** - Email notifications

### Database
- **PostgreSQL** - Primary database (production)
- **H2 Database** - In-memory database (testing)
- **Flyway** - Database schema migrations

### Security
- **JWT (JSON Web Tokens)** - Token-based authentication
- **BCrypt** - Password hashing
- **Spring Security OAuth2** - OAuth support
- **CORS** - Cross-origin resource sharing

### Integrations
- **Razorpay API** - Payment processing
- **AWS S3** - File storage (optional)
- **SendGrid/Gmail** - Email service

### Build & Deployment
- **Maven** - Build and dependency management
- **Docker** - Containerization
- **Render/Heroku** - Cloud deployment

### Development Tools
- **Lombok** - Reduce boilerplate code
- **Swagger/OpenAPI** - API documentation
- **SLF4J** - Logging framework
- **Spring Boot Actuator** - Monitoring and health checks

### Testing
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework
- **Spring Boot Test** - Integration testing

---

## 📚 API Documentation

### Authentication Endpoints

```
POST    /auth/register                  # User registration
POST    /auth/vendor-register           # Vendor registration
POST    /auth/admin-register            # Admin registration
POST    /auth/login                     # User login
POST    /auth/verify-otp                # OTP verification
POST    /auth/refresh-token             # Refresh JWT token
POST    /auth/forgot-password           # Request password reset
POST    /auth/reset-password            # Reset password with OTP
GET     /auth/profile                   # Get current user profile
PUT     /auth/profile                   # Update user profile
POST    /auth/logout                    # Logout
```

### Product Endpoints

```
GET     /api/products                   # Get all products (paginated)
GET     /api/products/{id}              # Get product by ID
GET     /api/products/search            # Search products
POST    /api/products                   # Create product (vendor)
PUT     /api/products/{id}              # Update product (vendor)
DELETE  /api/products/{id}              # Delete product (vendor)
GET     /api/products/category/{catId}  # Get products by category
GET     /api/products/vendor/{vendorId} # Get vendor products
POST    /api/products/{id}/upload-image # Upload product image
GET     /api/products/{id}/images       # Get product images
```

### Cart Endpoints

```
GET     /api/cart                       # Get user cart
POST    /api/cart/add                   # Add item to cart
PUT     /api/cart/update/{itemId}       # Update cart item
DELETE  /api/cart/remove/{itemId}       # Remove item from cart
DELETE  /api/cart/clear                 # Clear entire cart
POST    /api/cart/apply-coupon          # Apply coupon
DELETE  /api/cart/remove-coupon         # Remove coupon
POST    /api/cart/checkout              # Proceed to checkout
```

### Order Endpoints

```
GET     /api/orders                     # Get user orders (paginated)
GET     /api/orders/{id}                # Get order by ID
POST    /api/orders                     # Create order
PUT     /api/orders/{id}                # Update order
DELETE  /api/orders/{id}                # Delete order
POST    /api/orders/{id}/cancel         # Cancel order
POST    /api/orders/{id}/return         # Return order
GET     /api/orders/track/{orderNumber} # Track order
PUT     /api/orders/{id}/status         # Update order status
GET     /api/orders/vendor/my-orders    # Get vendor orders
```

### Payment Endpoints

```
POST    /api/payments/create-order      # Create Razorpay order
POST    /api/payments/verify            # Verify payment
GET     /api/payments/history           # Get payment history
POST    /api/payments/{id}/refund       # Request refund
```

### Category Endpoints

```
GET     /api/categories                 # Get all categories
GET     /api/categories/{id}            # Get category by ID
POST    /api/categories                 # Create category (admin/employee)
PUT     /api/categories/{id}            # Update category
DELETE  /api/categories/{id}            # Delete category
GET     /api/categories/hierarchy       # Get category hierarchy
```

### Admin Endpoints

```
GET     /api/admin/dashboard            # Dashboard stats
GET     /api/admin/users                # Get all users
GET     /api/admin/vendors              # Get all vendors
GET     /api/admin/products             # Get all products
POST    /api/admin/vendors/{id}/approve # Approve vendor
POST    /api/admin/products/{id}/approve # Approve product
GET     /api/admin/analytics            # Analytics data
PUT     /api/admin/config               # Update system config
```

For complete API documentation, visit: **https://indiantrademart-backend.onrender.com/swagger-ui.html**

---

## 📁 Project Structure

```
indiantrademartmain-backend-main/
├── src/main/java/com/indiantrademart/
│   ├── auth/                     # Authentication module
│   ├── user/                     # User management
│   ├── product/                  # Product management
│   ├── cart/                     # Shopping cart
│   ├── order/                    # Order management
│   ├── payment/                  # Payment processing
│   ├── vendor/                   # Vendor management
│   ├── admin/                    # Admin management
│   ├── employee/                 # Employee module
│   ├── category/                 # Category management
│   ├── location/                 # Location management
│   ├── file/                     # File upload
│   ├── security/                 # Security config
│   ├── config/                   # Application config
│   ├── exception/                # Exception handling
│   ├── dto/                      # Data transfer objects
│   ├── util/                     # Utility classes
│   └── ApplicationMain.java      # Entry point
├── src/main/resources/
│   ├── application.properties
│   ├── application-render.properties
│   ├── db/migration/             # Flyway migrations
│   └── log4j2.xml
├── pom.xml                       # Maven configuration
├── Dockerfile
└── README.md                     # This file
```

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17+** - JDK (Java Development Kit)
- **Maven 3.8+** - Build tool
- **PostgreSQL 13+** - Database (for production)
- **Git** - Version control

---

## 🚀 Installation & Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/dipanshupandey95/indiantrademart-backend.git
cd indiantrademartmain-backend-main
```

### Step 2: Install Dependencies

```bash
mvn clean install
```

### Step 3: Configure Environment Variables

```bash
cp .env.example .env
```

Edit `.env` with your configuration.

### Step 4: Create Database

```bash
createdb indiantrademart
```

---

## 🔧 Configuration

### Environment Variables

```env
# Application
APP_NAME=Indian Trade Mart
ENVIRONMENT=production

# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/indiantrademart
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# Server
SERVER_PORT=8080
SERVER_SERVLET_CONTEXT_PATH=/api/v1

# JWT
JWT_SECRET={{your_secret_key_here}}
JWT_EXPIRATION=86400000

# CORS
ALLOWED_ORIGINS=https://indiantrademart.com,http://localhost:3000

# Razorpay
RAZORPAY_KEY_ID={{your_razorpay_key}}
RAZORPAY_KEY_SECRET={{your_razorpay_secret}}

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME={{your_email}}
MAIL_PASSWORD={{your_app_password}}
```

---

## 💻 Running the Application

### Development Mode

```bash
mvn clean spring-boot:run
```

### Production Mode

```bash
mvn clean package
java -jar target/indiantrademart-backend-1.0.jar
```

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

---

## 🗄️ Database

### Migrations

Database migrations are managed by Flyway. New migrations go in: `src/main/resources/db/migration/`

### Backup

```bash
pg_dump -U postgres indiantrademart > backup.sql
```

---

## 🔐 Authentication

### JWT Token Flow

1. User login with email/phone + password
2. Server generates JWT token
3. Client includes token in `Authorization: Bearer {token}` header
4. Server validates token on each request

### Token Refresh

```bash
POST /auth/refresh-token
Content-Type: application/json

{
  "refreshToken": "..."
}
```

---

## ⚠️ Error Handling

### Standard Error Response

```json
{
  "success": false,
  "error": {
    "code": "INVALID_REQUEST",
    "message": "Invalid email format"
  },
  "timestamp": "2025-11-02T10:30:00Z"
}
```

---

## 🚀 Deployment

### Deploy to Render

1. Push to GitHub
2. Create Render Web Service
3. Set environment variables
4. Deploy

**Build Command:** `mvn clean package`  
**Start Command:** `java -jar target/*.jar`

---

## 🔍 Troubleshooting

### Database Connection Failed

1. Verify database is running
2. Check connection string
3. Verify database credentials

### Port 8080 Already in Use

```bash
java -jar target/*.jar --server.port=8081
```

### Keep-Alive Service Causing 502 Errors

This is fixed in `application-render.properties`:
```properties
app.keep-alive.enabled=false
```

### CORS Error

Verify frontend URL in ALLOWED_ORIGINS environment variable.

---

## 👤 Author

**Dipanshu Kumar Pandey**

---

**API URL:** [https://indiantrademart-backend.onrender.com/api/v1](https://indiantrademart-backend.onrender.com/api/v1)  
**Last Updated:** November 2, 2025  
**Status:** Production Ready ✅
