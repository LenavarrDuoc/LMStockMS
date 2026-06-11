#Stage 1: Builder, compilacion
#imagen base con el kit jdk 21
FROM eclipse-temurin:21-jdk AS builder

#establece directorio de trabajo
WORKDIR /app

#copia primero el pom al directorio actual (/app) para aprovechar cache
#de esta forma si el pom no cambia docker lo reutiliza y no vuelve a descargar dependencias
COPY pom.xml .

#copia el Maven Wrapper (mvnw). Es un script que descarga y ejecuta Maven
#se copia antes que el codigo fuente por las mismas razon de pom.xml
COPY mvnw .

#configuraciones de maven wrapper para que mvnw sepa que version de maven descargar
#(.mvn/wrapper/maven-wrapper.properties)
COPY .mvn .mvn

#da permiso de ejecucion al script mvnw && pre-descarga las dependecias del pom.xml
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

#copia el codigo fuente, al ser el que estara en mas cambio solo afectara
#a las capas por debajo de esta
COPY src ./src

#compila y empaqueta la aplicacion a JAR saltando Tests
RUN ./mvnw clean package -DskipTests -q

#Stage 2: Runtime
#imagen base solo con jre para ejecutar el jar
FROM eclipse-temurin:21-jre

WORKDIR /app

#copia del stage anterior el JAR generado
COPY --from=builder /app/target/*.jar app.jar

#expone puerto aleatorio al estar trabajando con eureka
EXPOSE 0

#comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]