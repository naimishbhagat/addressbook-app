FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN addgroup --system spring && adduser --system --ingroup spring spring
COPY --from=build /app/target/addressbook-app.jar addressbook-app.jar
RUN chown spring:spring addressbook-app.jar
USER spring:spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "addressbook-app.jar"]
