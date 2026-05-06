package com.hospital.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    private static final String WINDOWS_AUTH_URL =
            "jdbc:sqlserver://DESKTOP-62DQD2A\\SQLEXPRESS:1433;databaseName=hospital_db;integratedSecurity=true;trustServerCertificate=true;";

    private static final String SQL_AUTH_URL_TEMPLATE =
            "jdbc:sqlserver://DESKTOP-62DQD2A\\SQLEXPRESS:1433;databaseName=hospital_db;user=%s;password=%s;trustServerCertificate=true;";

    private DBConnection() {
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = createConnection();
            }
            return connection;
        } catch (SQLException e) {
            throw new SQLException(buildHelpfulMessage(e), e);
        }
    }

    private Connection createConnection() throws SQLException {
        // 1. Define the URL (No integratedSecurity=true)
        // Note: Keep \\SQLEXPRESS, remove :1433 to let it find the port automatically
        String url = "jdbc:sqlserver://DESKTOP-62DQD2A\\SQLEXPRESS;" +
                "databaseName=hospital_db;" +
                "encrypt=true;" +
                "trustServerCertificate=true;";

        // 2. Define the credentials you just created
        String user = "hospital_user";
        String password = "12345678";

        // 3. Connect using User/Pass
        return DriverManager.getConnection(url, user, password);
    }

    private String buildHelpfulMessage(SQLException e) {
        String msg = e.getMessage() == null ? "" : e.getMessage();
        String lower = msg.toLowerCase();
        boolean looksLikeNativeAuth =
                lower.contains("native") ||
                        lower.contains("loadlibrary") ||
                        lower.contains("authenticationjni") ||
                        lower.contains("dll");

        if (looksLikeNativeAuth) {
            return "SQL Server Windows authentication failed due to native authentication.\n" +
                    "Run the app with native access enabled:\n" +
                    "  --enable-native-access=ALL-UNNAMED\n" +
                    "Then try login again.\n" +
                    "If you cannot enable native access, set env vars DB_USER and DB_PASSWORD for SQL authentication fallback.";
        }

        return "Unable to connect to SQL Server.\n" +
                "- Verify SQL Server instance: DESKTOP-62DQD2A\\SQLEXPRESS\n" +
                "- Verify SQL Server Browser/service/port 1433\n" +
                "- If using Windows auth, enable native access for mssql-jdbc on JDK 24+:\n" +
                "  --enable-native-access=ALL-UNNAMED\n" +
                "- Or set env vars DB_USER and DB_PASSWORD for SQL authentication fallback.";
    }
}