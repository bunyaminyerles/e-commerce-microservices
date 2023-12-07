FROM maven:3.8.4-openjdk-17-slim AS builder
WORKDIR /
COPY pom.xml ./pom.xml
RUN mvn dependency:go-offline
COPY src/ ./src/
RUN mvn clean install -Dmaven.test.skip=true

FROM openjdk:17-jdk-slim AS runner
WORKDIR /opt/order-service
COPY --from=builder target/app.jar /opt/order-service/app.jar
RUN chown -R nobody /opt/order-service
USER nobody
EXPOSE 8083
CMD ["sh", "-c","java -jar /opt/order-service/app.jar"]