# HospitalManagementSystem

Desktop Hospital Management System built with Java 17, Swing, SQL Server, JDBC, and MVC architecture.

## Prerequisites
- Java 17 JDK
- Microsoft SQL Server (Express/Developer)
- SQL Server JDBC Driver (`mssql-jdbc-12.4.2.jre11.jar`) or Maven dependency

## Setup
1. Run `hospital_db.sql` in SQL Server Management Studio.
2. Update SQL Server host/instance in `src/com/hospital/utils/DBConnection.java` if needed.
3. Build and run:
   - Maven: `mvn compile exec:java -Dexec.mainClass=\"com.hospital.main.Main\"`
   - Or compile manually and run `com.hospital.main.Main`.

## Default Logins
- `admin` / `admin123`
- `receptionist1` / `rec123`
- `doctor1` / `doc123`

## Screenshots
<img width="1010" height="630" alt="image" src="https://github.com/user-attachments/assets/3205bb91-ecdc-41cb-8a58-97e3256e6344" />


## Project Structure
- `src/com/hospital/main` - Application entry point
- `src/com/hospital/model` - Data models
- `src/com/hospital/dao` - JDBC DAOs (PreparedStatement + try-with-resources)
- `src/com/hospital/controller` - Business logic controllers
- `src/com/hospital/ui` - Main frames
- `src/com/hospital/ui/panels` - Module panels
- `src/com/hospital/utils` - DB connection, validators, IDs, theme
