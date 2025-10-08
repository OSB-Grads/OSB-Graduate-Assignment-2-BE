# ===== Stage 1: Build the app =====
FROM maven:3.9.11-eclipse-temurin-21 AS BUILD

WORKDIR /app

# Copying mvn wrapper and pom.xml to cache dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the project(skipping tests)
RUN ./mvnw clean package -DskipTests


# ===== Stage 2: Run the app =====
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

#Copying jar built in previous stage
COPY --from=build /app/target/*.jar app.jar

#Exposing the port on which our backend runs
EXPOSE 8080

#Start the application
ENTRYPOINT ["java" , "-jar" , "app.jar"]