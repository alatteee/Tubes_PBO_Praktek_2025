import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import dao.DatabaseConnection;

public class ConnectionTest {

    @Test
    void testDatabaseConnection() {
        assertDoesNotThrow(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
            }
        });
    }
}
