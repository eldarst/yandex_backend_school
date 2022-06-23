FROM openjdk:11
COPY ./target/YandexMarket-0.0.1-SNAPSHOT.jar YandexMarket-0.0.1-SNAPSHOT.jar
EXPOSE 80
ENTRYPOINT ["java","-jar","YandexMarket-0.0.1-SNAPSHOT.jar"]