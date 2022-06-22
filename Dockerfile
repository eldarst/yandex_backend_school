FROM openjdk:11
WORKDIR /YandexMarket
COPY . .
RUN mvn clean install
CMD mvn spring-boot:run