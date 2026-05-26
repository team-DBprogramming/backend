FROM eclipse-temurin:17-jdk AS builder

WORKDIR /workspace

COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
COPY src ./src

RUN chmod +x ./gradlew && ./gradlew bootJar -x test
RUN JAR_FILE="$(find build/libs -name '*.jar' ! -name '*-plain.jar' | head -n 1)" && cp "$JAR_FILE" app.jar

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /workspace/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
