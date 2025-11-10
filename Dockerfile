# --- Build stage ---
FROM gradle:8.10-jdk21 AS build
WORKDIR /app

# 의존성 캐시
COPY gradlew build.gradle settings.gradle ./
COPY gradle gradle
RUN ./gradlew dependencies --no-daemon || true

# 전체 복사 및 빌드
COPY . .
RUN ./gradlew clean bootJar -x test --no-daemon

# --- Run stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=/app/build/libs/*.jar
COPY --from=build ${JAR_FILE} app.jar

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+HeapDumpOnOutOfMemoryError"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]