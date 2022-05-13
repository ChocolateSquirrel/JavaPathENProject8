FROM openjdk:8
EXPOSE 8080
COPY build/libs/ms-user-1.0.0.jar user.jar
ENTRYPOINT ["java", "-jar", "/user.jar"]