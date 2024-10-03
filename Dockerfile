FROM gradle:jdk17 as gradlejdk

COPY ./ ./

RUN gradle build

RUN mv ./build/libs/MMOMineSweeper-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8080
CMD ["java", "-jar", "/app.jar"]