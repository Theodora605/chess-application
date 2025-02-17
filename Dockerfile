FROM openjdk:17-alpine

EXPOSE 8080

WORKDIR /app

COPY ./server/target/server-3.4.1.jar .

CMD ["java","-jar","/app/server-3.4.1.jar"]