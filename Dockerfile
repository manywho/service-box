FROM java:8

EXPOSE 8080

CMD ["java", "-jar", "box-3.0-SNAPSHOT.jar"]

WORKDIR /app

ADD . ./
