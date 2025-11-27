package model;

import strategy.payment.PaymentStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Receipt {

    private String transactionId;
    private LocalDateTime transactionTime;
    private ServiceOrder order;
    private PaymentStrategy payment;
    private String pdfFilePath;

    /**
     * Constructor lengkap sesuai spec:
     * pdfFilePath boleh null, nanti diisi otomatis oleh saveToPDF().
     * DITAMBAH EXCEPTION HANDLING
     */
    public Receipt(String transactionId,
                   LocalDateTime transactionTime,
                   ServiceOrder order,
                   PaymentStrategy payment,
                   String pdfFilePath) {

        // ========== VALIDASI WAJIB ==========
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Receipt Error: transactionId tidak boleh kosong.");
        }
        if (transactionTime == null) {
            throw new IllegalArgumentException("Receipt Error: transactionTime tidak boleh null.");
        }
        if (order == null) {
            throw new IllegalArgumentException("Receipt Error: order tidak boleh null.");
        }
        if (payment == null) {
            throw new IllegalArgumentException("Receipt Error: payment tidak boleh null.");
        }

        this.transactionId = transactionId;
        this.transactionTime = transactionTime;
        this.order = order;
        this.payment = payment;
        this.pdfFilePath = pdfFilePath;
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public LocalDateTime getTransactionTime() { return transactionTime; }
    public ServiceOrder getOrder() { return order; }
    public PaymentStrategy getPayment() { return payment; }
    public String getPdfFilePath() { return pdfFilePath; }

    /**
     * Setter PDF path — DITAMBAH VALIDASI
     */
    public void setPdfFilePath(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Receipt Error: pdfFilePath tidak boleh kosong/null.");
        }
        this.pdfFilePath = path;
    }

    /**
     * Membuat teks struk.
     * DITAMBAH VALIDASI agar tidak NPE saat akses order atau komponen lain.
     */
    public String generateReceiptText() {

        if (order == null) {
            throw new IllegalStateException("Receipt Error: Order belum di-set.");
        }
        if (order.getService() == null) {
            throw new IllegalStateException("Receipt Error: Layanan pada order tidak boleh null.");
        }
        if (order.getTotalCost() <= 0) {
            throw new IllegalStateException("Receipt Error: Total biaya belum dihitung.");
        }

        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // ==== HEADER ====
        sb.append("         PET CARE SYSTEM RECEIPT         \n");
        sb.append("        Politeknik Negeri Bandung        \n");
        sb.append("========================================\n");

        // ==== INFO TRANSAKSI ====
        sb.append(String.format("%-12s: %s%n", "Transaction", transactionId));
        sb.append(String.format("%-12s: %s%n", "Date/Time",
                dtf.format(transactionTime)));
        sb.append("----------------------------------------\n");

        // ==== INFO ORDER ====
        sb.append("ORDER INFO\n");

        sb.append(String.format("%-12s: %s%n", "Order ID", order.getOrderId()));

        if (order.getCustomer() != null) {
            sb.append(String.format("%-12s: %s%n",
                    "Customer", order.getCustomer().getName()));
        }

        if (order.getPet() != null) {
            sb.append(String.format("%-12s: %s (%s)%n",
                    "Pet",
                    order.getPet().getName(),
                    order.getPet().getClass().getSimpleName()));
        }

        sb.append(String.format("%-12s: %s%n",
                "Service",
                order.getService().getName()));

        sb.append(String.format("%-12s: Rp %.0f%n",
                "Total",
                order.getTotalCost()));

        // ==== PAYMENT ====
        sb.append(String.format("%-12s: %s%n",
                "Payment",
                payment.getName()));

        sb.append("========================================\n");
        sb.append("           THANK YOU FOR VISITING        \n");
        sb.append("          Thank you for your trust!      \n");
        sb.append("========================================\n");

        return sb.toString();
    }

    // ======================= PDF UTILS =======================
    private String escapePdfText(String text) {
        if (text == null) return "";
        return text
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    // ======================= SAVE AS PDF =======================

    /**
     * Ditambah exception handling untuk directory, file IO, dan parsing.
     */
    public void saveToPDF() {

        // Pastikan transactionId valid
        String safeId = transactionId != null ? transactionId : "TRX-" + System.currentTimeMillis();

        // Cek folder receipts
        String directoryPath = "receipts";
        File dir = new File(directoryPath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("ERROR: Gagal membuat folder receipts.");
        }

        // Tentukan file output
        String fileName = "receipt_" + safeId + ".pdf";
        File outFile = new File(dir, fileName);
        this.pdfFilePath = outFile.getAbsolutePath();

        // Generate text — ini juga sudah punya exception
        String[] lines;
        try {
            lines = generateReceiptText().split("\\r?\\n");
        } catch (Exception e) {
            throw new RuntimeException("Gagal membuat teks struk: " + e.getMessage(), e);
        }

        // Bangun content PDF
        StringBuilder content = new StringBuilder();
        content.append("BT\n");
        content.append("/F1 12 Tf\n");
        content.append("72 800 Td\n");
        content.append("18 TL\n");

        for (String line : lines) {
            content.append("(")
                   .append(escapePdfText(line))
                   .append(") Tj\n");
            content.append("T*\n");
        }
        content.append("ET\n");

        byte[] contentBytes = content.toString().getBytes(StandardCharsets.ISO_8859_1);
        int contentLength = contentBytes.length;

        // ==== I/O Handling ====
        try (FileOutputStream fos = new FileOutputStream(outFile)) {

            // (kode PDF builder kamu tetap dipertahankan)
            // hanya exception di luar yang dibungkus RuntimeException

            String header = "%PDF-1.4\n" +
                            "%\u00e2\u00e3\u00cf\u00d3\n";

            List<String> objects = new ArrayList<>();

            // original objects (tidak dihapus, hanya diteruskan)
            objects.add("1 0 obj\n" +
                       "<< /Type /Catalog /Pages 2 0 R >>\n" +
                       "endobj\n");

            objects.add("2 0 obj\n" +
                       "<< /Type /Pages /Kids [3 0 R] /Count 1 >>\n" +
                       "endobj\n");

            objects.add("3 0 obj\n" +
                       "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842]\n" +
                       "   /Resources << /Font << /F1 4 0 R >> >>\n" +
                       "   /Contents 5 0 R >>\n" +
                       "endobj\n");

            objects.add("4 0 obj\n" +
                       "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\n" +
                       "endobj\n");

            StringBuilder obj5 = new StringBuilder();
            obj5.append("5 0 obj\n");
            obj5.append("<< /Length ").append(contentLength).append(" >>\n");
            obj5.append("stream\n");
            obj5.append(content);
            obj5.append("endstream\n");
            obj5.append("endobj\n");

            objects.add(obj5.toString());

            StringBuilder pdfBuilder = new StringBuilder();
            pdfBuilder.append(header);

            List<Integer> offsets = new ArrayList<>();
            int offset = header.getBytes(StandardCharsets.ISO_8859_1).length;

            for (String obj : objects) {
                offsets.add(offset);
                pdfBuilder.append(obj);
                offset = pdfBuilder.toString().getBytes(StandardCharsets.ISO_8859_1).length;
            }

            int startXref = pdfBuilder.toString().getBytes(StandardCharsets.ISO_8859_1).length;
            int objCount = objects.size();

            StringBuilder xref = new StringBuilder();
            xref.append("xref\n");
            xref.append("0 ").append(objCount + 1).append("\n");
            xref.append("0000000000 65535 f \n");
            for (int off : offsets) {
                xref.append(String.format("%010d 00000 n \n", off));
            }

            StringBuilder trailer = new StringBuilder();
            trailer.append("trailer\n");
            trailer.append("<< /Size ").append(objCount + 1).append(" /Root 1 0 R >>\n");
            trailer.append("startxref\n");
            trailer.append(startXref).append("\n");
            trailer.append("%%EOF\n");

            pdfBuilder.append(xref);
            pdfBuilder.append(trailer);

            byte[] pdfBytes = pdfBuilder.toString().getBytes(StandardCharsets.ISO_8859_1);
            fos.write(pdfBytes);

        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan receipt ke file: " + pdfFilePath, e);
        }
    }
}
