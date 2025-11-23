package dao;

import model.Receipt;
import model.ServiceOrder;
import strategy.payment.CashPayment;
import strategy.payment.EWalletPayment;
import strategy.payment.PaymentStrategy;
import strategy.payment.TransferPayment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcReceiptDAO implements ReceiptDAO {

    private final ServiceOrderDAO serviceOrderDAO = new JdbcServiceOrderDAO();

    // ===================== HELPER =====================

    private PaymentStrategy mapPaymentStrategy(String method) {
        if (method == null) return null;
        String m = method.toLowerCase();
        if (m.contains("cash")) {
            return new CashPayment();
        } else if (m.contains("transfer")) {
            return new TransferPayment();
        } else if (m.contains("wallet") || m.contains("e-wallet") || m.contains("ewallet")) {
            return new EWalletPayment();
        } else {
            // default ke cash
            return new CashPayment();
        }
    }

    private Receipt mapRowToReceipt(ResultSet rs) throws SQLException {
        String transactionId = rs.getString("transaction_id");
        String orderId = rs.getString("order_id");
        String paymentMethod = rs.getString("payment_method");
        String pdfPath = rs.getString("pdf_path");
        Timestamp ts = rs.getTimestamp("transaction_time");

        LocalDateTime time = ts != null ? ts.toLocalDateTime() : null;

        // Ambil ServiceOrder dari DAO lain
        ServiceOrder order = serviceOrderDAO.findById(orderId);

        PaymentStrategy paymentStrategy = mapPaymentStrategy(paymentMethod);

        return new Receipt(
                transactionId,
                time,
                order,
                paymentStrategy,
                pdfPath
        );
    }

    // ===================== IMPLEMENTASI DAO =====================

    @Override
    public Receipt save(Receipt receipt) {
        String sql = """
                INSERT INTO receipts
                (transaction_id, order_id, payment_method, pdf_path, transaction_time)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, receipt.getTransactionId());
            ps.setString(2, receipt.getOrder().getOrderId());
            ps.setString(3, receipt.getPayment() != null ? receipt.getPayment().getName() : null);
            ps.setString(4, receipt.getPdfFilePath());
            ps.setTimestamp(5, Timestamp.valueOf(receipt.getTransactionTime()));

            ps.executeUpdate();
            return receipt;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal menyimpan receipt: " + e.getMessage(), e);
        }
    }

    @Override
    public Receipt findById(String transactionId) {
        String sql = "SELECT * FROM receipts WHERE transaction_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, transactionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToReceipt(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal mencari receipt dengan id: " + transactionId, e);
        }
    }

    @Override
    public List<Receipt> findByOrder(ServiceOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Order tidak boleh null");
        }

        String sql = "SELECT * FROM receipts WHERE order_id = ? ORDER BY transaction_time DESC";

        List<Receipt> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, order.getOrderId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.add(mapRowToReceipt(rs));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil receipt untuk order: " + order.getOrderId(), e);
        }
    }
}
