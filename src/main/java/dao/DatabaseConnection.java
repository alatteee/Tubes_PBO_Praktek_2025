package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final String PROPERTIES_FILE = "db.properties";

    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try {
            // Load file db.properties dari classpath (src/main/resources)
            InputStream in = DatabaseConnection.class
                    .getClassLoader()
                    .getResourceAsStream(PROPERTIES_FILE);

            if (in == null) {
                throw new RuntimeException("File konfigurasi " + PROPERTIES_FILE + " tidak ditemukan di classpath.");
            }

            Properties props = new Properties();
            props.load(in);

            URL = props.getProperty("url");
            USER = props.getProperty("user");
            PASSWORD = props.getProperty("password");

            if (URL == null || USER == null || PASSWORD == null) {
                throw new RuntimeException("Property url/user/password belum lengkap di " + PROPERTIES_FILE);
            }

            // Pastikan driver PostgreSQL tersedia
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver PostgreSQL ditemukan.");

        } catch (Exception e) {
            throw new RuntimeException("Gagal menginisialisasi konfigurasi DatabaseConnection.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static Connection getUncheckedConnection() {
        try {
            return getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal terhubung ke database. Cek konfigurasi di db.properties.", e);
        }
    }
}
