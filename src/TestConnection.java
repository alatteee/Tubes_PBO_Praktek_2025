import java.sql.Connection;
import dao.DatabaseConnection;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Koneksi BERHASIL!");
        } catch (Exception e) {
            System.out.println("Koneksi GAGAL!");
            e.printStackTrace();
        }
    }
}
