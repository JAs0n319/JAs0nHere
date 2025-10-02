# syntax=docker/dockerfile:1

# === 构建阶段：Gradle + JDK21 ===
FROM gradle:8.10.2-jdk21-alpine AS build
WORKDIR /workspace

# 利用缓存：先拷配置文件
COPY build.gradle settings.gradle ./
COPY gradle.properties* ./

# 再拷源码
COPY src ./src

# 使用 BuildKit 缓存（需 Docker 新版）
RUN --mount=type=cache,target=/home/gradle/.gradle \
    gradle --no-daemon clean bootJar

# === 运行阶段：JRE 21 仅带产物 ===
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# 可选：健康检查才需要
RUN apk add --no-cache curl

# 复制构建产物（我们在 build.gradle 里把文件名固定为 app.jar 更稳）
COPY --from=build /workspace/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh","-c","java ${JAVA_OPTS} -jar /app/app.jar"]