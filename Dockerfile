FROM eclipse-temurin:17-jdk-alpine

RUN apk add --no-cache ffmpeg

WORKDIR /app

COPY build/libs/video_player_be-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java","-jar","app.jar"]