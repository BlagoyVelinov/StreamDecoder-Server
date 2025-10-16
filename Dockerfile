FROM gradle:8.2-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

FROM eclipse-temurin:17-jdk-alpine

RUN apk add --no-cache ffmpeg ffmpeg-libs

WORKDIR /app

RUN mkdir -p /app/streams && chmod 777 /app/streams

COPY --from=build /app/build/libs/video_player_be-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]