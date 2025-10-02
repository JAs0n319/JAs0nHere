FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} /app/app.jar

EXPOSE 8080
ENTRYPOINT ["sh","-c","java ${JAVA_OPTS} -jar /app/app.jar"]
