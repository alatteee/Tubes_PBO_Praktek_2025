import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

import dao.DatabaseConnection;

public class DriverTest {

    @Test
    void testPostgresDriverAvailable() {
        // Pastikan class driver PostgreSQL ada di classpath
        assertDoesNotThrow(
                () -> Class.forName("org.postgresql.Driver"),
                "Driver PostgreSQL seharusnya tersedia di classpath"
        );
    }

    @Test
    void testSupabaseConnectionMetadata() {
        assertDoesNotThrow(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                assertNotNull(conn, "Koneksi tidak boleh null");
                String productName = conn.getMetaData().getDatabaseProductName();
                assertNotNull(productName, "Nama produk database tidak boleh null");
                // Biasanya "PostgreSQL"
                assertTrue(productName.toLowerCase().contains("postgres"),
                        "Database harusnya PostgreSQL, tapi dapat: " + productName);
            }
        });
    }
}
