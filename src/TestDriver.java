import dao.DatabaseConnection;
import java.sql.Connection;

public class TestDriver {
    public static void main(String[] args) {
        try {
            // Test driver
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver PostgreSQL ditemukan!");

            // Test koneksi Supabase
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("Berhasil konek ke Supabase → " + conn.getMetaData().getDatabaseProductName());
            
            conn.close();
        } catch (Exception e) {
            System.out.println("Terjadi error:");
            e.printStackTrace();
        }
    }
}
