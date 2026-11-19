FROM eclipse-temurin:25
# Run the application as its own user instead of root
RUN useradd --system travelblog
USER travelblog
EXPOSE 8080
COPY backend-target/travelBlog.jar travelBlog.jar
ENTRYPOINT ["java", "-jar", "travelBlog.jar"]
