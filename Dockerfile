# Runtime image
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# 直接复制已构建好的 jar
COPY build/libs/*.jar app.jar

# 复制应用配置
COPY src/main/resources/application.yaml ./config/

# 端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动命令，使用环境变量配置数据库
ENTRYPOINT ["java", "-jar", "app.jar", \
    "--spring.datasource.url=${DB_URL}", \
    "--spring.datasource.username=${DB_USERNAME}", \
    "--spring.datasource.password=${DB_PASSWORD}"]
