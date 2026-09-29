How to Run the Software Manually
Whenever you want to start or stop the application yourself:

Option 1: Using the Maven Wrapper in PowerShell / Terminal (Recommended)
Open a PowerShell or Command Prompt terminal.
Navigate to the project folder:
powershell
cd "c:\Users\dhara\OneDrive\windows apps\TripSplit\TripSplit"
Run the Spring Boot application:
powershell
.\mvnw.cmd spring-boot:run
To stop the application, press Ctrl + C in the terminal.
Option 2: Running from VS Code / IDE
Open the main class: 

TripSplitApplication.java
.
Click the Run or Debug button appearing above the main method:
java
public static void main(String[] args) {
    SpringApplication.run(TripSplitApplication.class, args);
}
Option 3: Building and Running the Executable JAR
Build the production .jar file:
powershell
cd "c:\Users\dhara\OneDrive\windows apps\TripSplit\TripSplit"
.\mvnw.cmd clean package -DskipTests
Run the compiled JAR:
powershell
java -jar target\TripSplit-0.0.1-SNAPSHOT.jar
Configuration & Settings
Application settings (such as server port 8080, database connection, and Swagger paths) are configured in:



TripSplit is currently running in the background on port 8080.

You can immediately open and use:

Web UI: http://localhost:8080/
Swagger / OpenAPI Documentation: http://localhost:8080/swagger-ui.html
H2 In-Memory Database Console: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:tripsplitdb
Username: sa
Password: (leave empty)
