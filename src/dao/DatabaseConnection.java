package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
        "jdbc:postgresql://aws-1-ap-northeast-1.pooler.supabase.com:5432/postgres?sslmode=require";

    private static final String USER = "postgres.ggofowxbxcpqhxjukmbd"; 
    private static final String PASSWORD = "jagogodot";

    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver PostgreSQL ditemukan.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver PostgreSQL TIDAK ditemukan di classpath!", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
