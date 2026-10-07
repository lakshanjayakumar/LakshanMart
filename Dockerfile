# Multi-stage Dockerfile for LakshanMart (Maven + Tomcat 10 JDK 17)

# Stage 1: Build phase
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven POM and source code
COPY pom.xml .
COPY src ./src

# Compile and package WAR archive
RUN mvn clean package -DskipTests

# Stage 2: Runtime phase with Apache Tomcat 10 (Jakarta EE 10 / JDK 17)
FROM tomcat:10.1-jdk17
WORKDIR /usr/local/tomcat

# Remove default Tomcat sample webapps to ensure ROOT.war runs at root context (/)
RUN rm -rf webapps/*

# Copy the generated WAR to ROOT.war
COPY --from=build /app/target/LakshanMart.war webapps/ROOT.war

# Default HTTP port
EXPOSE 8080

# Dynamically bind to $PORT if provided by cloud platforms (Render, Railway), else fallback to 8080
CMD ["sh", "-c", "if [ -n \"$PORT\" ]; then sed -i \"s/port=\\\"8080\\\"/port=\\\"$PORT\\\"/g\" conf/server.xml; fi && catalina.sh run"]
