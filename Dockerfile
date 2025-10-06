FROM eclipse-temurin:17-jdk-jammy AS otel-agent-downloader

ENV OTEL_AGENT_VERSION=2.20.0
ENV OTEL_AGENT_DIR=/opt/javaagent

RUN mkdir -p ${OTEL_AGENT_DIR} && \
    curl -L -o ${OTEL_AGENT_DIR}/opentelemetry-javaagent.jar \
    https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar


FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=otel-agent-downloader /opt/javaagent/opentelemetry-javaagent.jar /opt/opentelemetry-javaagent.jar

COPY build/libs/*.jar app.jar
COPY src/main/resources/application.yaml ./config/

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-javaagent:/opt/opentelemetry-javaagent.jar", \
    "-Dloader.config.location=config/", \
    "-jar", "app.jar", \
    "--spring.datasource.url=${DB_URL}", \
    "--spring.datasource.username=${DB_USERNAME}", \
    "--spring.datasource.password=${DB_PASSWORD}"]
