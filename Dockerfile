###
# Build stage
#
FROM maven:3.8.2-jdk-11 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:11
COPY --from=build /home/app/target/demo-0.0.1-SNAPSHOT.jar /usr/local/lib/demo.jar
EXPOSE 80
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar"]
#FROM openjdk:11
#COPY ./target/YandexMarket-0.0.1-SNAPSHOT.jar YandexMarket-0.0.1-SNAPSHOT.jar
#EXPOSE 80
#ENTRYPOINT ["java","-jar","YandexMarket-0.0.1-SNAPSHOT.jar"]