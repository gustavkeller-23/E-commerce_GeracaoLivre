# Stage 1: Build da aplicação Java com Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia arquivo de dependências primeiro para cache do Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia código-fonte e compila pacote JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime enxuto e seguro
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Cria usuário não-root por segurança
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
