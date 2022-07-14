################################################
#             JAVA MICROSERVICE IMAGE          #
################################################
FROM maven:3.6.1-jdk-11-slim AS builder
WORKDIR /app
ENV MAVEN_OPTS=-Xss10M
COPY . /app
RUN mvn -e -B package -Dmaven.test.skip=true

FROM openjdk:11
WORKDIR /app
ENV JAVA_OPTS==$(JAVA_OPTS}
COPY --from=builder /app/any-authenticate-service-impl/target/any-authenticate-service-impl-0.0.1.jar /app/any-authenticate-service.jar
EXPOSE 8080
CMD java -jar /app/any-authenticate-service.jar ${ARGUMENTS}