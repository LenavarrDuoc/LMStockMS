FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Primero solo pom.xml y mvnw — cambian poco
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

# Luego el código fuente — cambia frecuente
COPY src ./src
RUN ./mvnw clean package -DskipTests -q

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 0
ENTRYPOINT ["java", "-jar", "app.jar"]