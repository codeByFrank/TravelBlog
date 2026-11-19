FROM eclipse-temurin:25
EXPOSE 8080
COPY backend-target/travelBlog.jar travelBlog.jar
ENTRYPOINT ["java", "-jar", "travelBlog.jar"]