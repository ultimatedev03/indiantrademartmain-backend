# Backend Local Development Setup

## Prerequisites
1. **Java 21** - Download from https://www.oracle.com/java/technologies/downloads/#java21
2. **Maven** - Download from https://maven.apache.org/download.cgi
3. **MySQL 8+** - Download from https://dev.mysql.com/downloads/mysql/
4. **Git** - Already installed

## Step 1: Setup Local MySQL Database

```bash
# Start MySQL service (Windows PowerShell as Admin)
net start MySQL80

# Or use MySQL command line
mysql -u root -p

# Create database and user
CREATE DATABASE itech_dev_db;
CREATE USER 'itech_user'@'localhost' IDENTIFIED BY 'itech_password_123';
GRANT ALL PRIVILEGES ON itech_dev_db.* TO 'itech_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

## Step 2: Verify Maven Installation

```powershell
mvn --version
```

## Step 3: Build Backend Locally

```powershell
cd "C:\Users\Dipanshu pandey\OneDrive\Desktop\ddd\indiantrademartmain-backend-main"

# Clean and build
mvn clean install -DskipTests

# If you get memory errors, use:
mvn clean install -DskipTests -Dmaven.ext.class.path=""
```

## Step 4: Run Backend Locally (Development Profile)

```powershell
# Using Maven Spring Boot plugin
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=development"

# Or run the JAR directly
java -jar target/itech-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=development
```

## Step 5: Check Backend is Running

```
http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

## Step 6: View Swagger/OpenAPI Documentation

```
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
```

## Troubleshooting

### Port Already in Use
```powershell
# Find and kill process on port 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Database Connection Error
- Check MySQL is running: `net start MySQL80`
- Verify credentials in `application.properties`
- Check database exists: `mysql -u root -p itech_dev_db`

### Maven Build Issues
- Clear Maven cache: `mvn clean`
- Check Java version: `java -version`
- Increase Maven memory: `set MAVEN_OPTS=-Xmx1024m`

### No application.yml or Database Properties
- Backend uses `application.properties` file in `src/main/resources`
- Default dev profile uses MySQL on localhost:3306
- If needed, override with env variables

## Running with Render Profile Locally (to test Render setup)

```powershell
# Set environment variables (in PowerShell)
$env:SPRING_PROFILES_ACTIVE="render"
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/itech_dev_db?useSSL=false&serverTimezone=Asia/Kolkata"
$env:SPRING_DATASOURCE_USERNAME="itech_user"
$env:SPRING_DATASOURCE_PASSWORD="itech_password_123"
$env:SPRING_DATASOURCE_DRIVER_CLASS_NAME="com.mysql.cj.jdbc.Driver"
$env:PORT="8080"

# Then run
mvn spring-boot:run
```

## Next Steps

1. Once backend runs successfully locally, check logs for any errors
2. Test an endpoint: `GET http://localhost:8080/api/v1/health`
3. If all works, fix issues in code
4. Commit changes: `git add -A && git commit -m "Fix local backend startup"`
5. Push to Render for production deployment
