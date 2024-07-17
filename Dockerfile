# Use Maven to build the application
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Use a slim JDK image to run the application
FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/MyTutor-0.0.1-SNAPSHOT.jar MyTutor.jar
EXPOSE 8080

# Set the Vietnamese time zone and start the application
ENTRYPOINT ["java", "-Duser.timezone=Asia/Ho_Chi_Minh", "-jar", "MyTutor.jar"]