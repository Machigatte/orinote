# Node.js builder for frontend
FROM node:18-alpine as node-builder

# 安装 git
RUN apk add --no-cache git

# Clone and build frontend
RUN git clone https://github.com/Machigatte/heartmanuscript.git /app/frontend
WORKDIR /app/frontend
RUN npm install && npm run build

# Java builder
FROM eclipse-temurin:17-jdk-jammy as builder

# Set working directory
WORKDIR /app

# Copy gradle files first for caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN mkdir -p src/main/resources/static

# Copy static files from frontend build
COPY --from=node-builder /app/frontend/out src/main/resources/static

RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew build --no-daemon

# Create final runtime image
FROM eclipse-temurin:17-jre-jammy

# Set working directory
WORKDIR /app

# Copy built jar from builder
COPY --from=builder /app/build/libs/*.jar app.jar

# Copy application configuration
COPY src/main/resources/application.yaml ./config/

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application with environment variables for DB config
ENTRYPOINT ["java", "-jar", "app.jar", \
    "--spring.datasource.url=${DB_URL}", \
    "--spring.datasource.username=${DB_USERNAME}", \
    "--spring.datasource.password=${DB_PASSWORD}"]
