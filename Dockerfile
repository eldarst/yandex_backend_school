FROM openjdk:11
ARG JAR_FILE=target/YandexMarket-0.0.1-SNAPSHOT.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
EXPOSE 80
ENTRYPOINT ["java","-jar","app.jar"]