FROM eclipse-temurin:17-jdk-alpine

RUN apk add --no-cache ffmpeg ffmpeg-libs

WORKDIR /app

RUN mkdir -p /app/streams && chmod 777 /app/streams

COPY build/libs/app.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java","-jar","app.jar"]