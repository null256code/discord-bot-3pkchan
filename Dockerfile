FROM amazoncorretto:17

RUN yum -y update && yum clean all

ARG JAR_FILE=app/build/docker/bot3pkchan.jar
COPY ${JAR_FILE} app.jar

ENV LANG ja_JP.UTF-8
ENV LANGUAGE ja_JP:UTF-8
ENV LC_ALL ja_JP.UTF-8
ENV TZ Asia/Tokyo

ENV JAVA_OPTS "-Xms256m -Xmx512m -Xss512k -XX:+UseContainerSupport"
ENTRYPOINT  ["java", "${JAVA_OPTS}", "-jar", "-Dserver.port=${PORT}", "/app.jar"]