FROM amazoncorretto:11
ARG JAR_FILE=app/build/docker/bot3pkchan.jar
COPY ${JAR_FILE} app.jar
ENV JAVA_OPTS "-Xms256m -Xmx512m -Xss512k"
ENTRYPOINT  ["java", "${JAVA_OPTS}", "-jar", "-Dserver.port=${PORT}", "/app.jar"]