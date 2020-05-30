FROM adoptopenjdk/openjdk12:alpine-jre

RUN mkdir /opt/app
COPY build/libs/planning-poker-spring-*.jar /opt/app/planning-poker.jar
EXPOSE 8080

CMD ["java", "-jar", "/opt/app/planning-poker.jar"]
