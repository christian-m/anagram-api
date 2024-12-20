# Stage 1: Cache Maven dependencies
FROM maven:3.9.9-eclipse-temurin-21-alpine AS cache
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

# Stage 2: Build Application
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY --from=cache /root/.m2 /root/.m2
COPY . .
RUN mvn clean package -Dmaven.test.skip=true

# Stage 3: Create the Runtime Image
FROM eclipse-temurin:21-alpine AS runtime
EXPOSE 8080
WORKDIR /app
COPY --from=build /app/target/*-with-dependencies.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
