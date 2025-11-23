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
     */
    public Receipt(String transactionId,
                   LocalDateTime transactionTime,
                   ServiceOrder order,
                   PaymentStrategy payment,
                   String pdfFilePath) {

        this.transactionId = transactionId;
        this.transactionTime = transactionTime;
        this.order = order;
        this.payment = payment;
        this.pdfFilePath = pdfFilePath;
    }

    // Getters

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public ServiceOrder getOrder() {
        return order;
    }

    public PaymentStrategy getPayment() {
        return payment;
    }

    public String getPdfFilePath() {
        return pdfFilePath;
    }

    public void setPdfFilePath(String path) {
        this.pdfFilePath = path;
    }

    /**
     * Membuat teks struk untuk ditampilkan di GUI atau disimpan ke file.
     */
    public String generateReceiptText() {
        StringBuilder sb = new StringBuilder();
    
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    
        // ==== HEADER TOKO / PET CARE ====
        sb.append("         PET CARE SYSTEM RECEIPT         \n");
        sb.append("        Politeknik Negeri Bandung        \n");
        sb.append("========================================\n");
    
        // ==== INFO TRANSAKSI ====
        sb.append(String.format("%-12s: %s%n", "Transaction", transactionId));
        sb.append(String.format("%-12s: %s%n", "Date/Time",
                dtf.format(transactionTime)));
        sb.append("----------------------------------------\n");
    
        // ==== INFO ORDER ====
        if (order != null) {
            sb.append("ORDER INFO\n");
    
            sb.append(String.format("%-12s: %s%n", "Order ID", order.getOrderId()));
    
            if (order.getCustomer() != null) {
                sb.append(String.format(
                        "%-12s: %s%n",
                        "Customer",
                        order.getCustomer().getName()
                ));
            }
    
            if (order.getPet() != null) {
                sb.append(String.format(
                        "%-12s: %s (%s)%n",
                        "Pet",
                        order.getPet().getName(),
                        order.getPet().getClass().getSimpleName()
                ));
            }
    
            if (order.getService() != null) {
                sb.append(String.format("%-12s: %s%n",
                        "Service",
                        order.getService().getName()));
            }
    
            sb.append(String.format("%-12s: Rp %.0f%n",
                    "Total",
                    order.getTotalCost()));
        }
    
        // ==== PAYMENT ====
        if (payment != null) {
            sb.append(String.format("%-12s: %s%n",
                    "Payment",
                    payment.getName()));
        }
    
        sb.append("========================================\n");
        sb.append("           THANK YOU FOR VISITING        \n");
        sb.append("          Thank you for your trust!      \n");
        sb.append("========================================\n");
    
        return sb.toString();
    }
    

    // ======================= PDF UTILS =======================

    /**
     * Escape karakter khusus untuk teks di PDF.
     */
    private String escapePdfText(String text) {
        if (text == null) return "";
        return text
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    // ======================= SAVE AS REAL PDF =======================

    /**
     * Generate file PDF beneran (satu halaman, teks struk).
     * Tanpa library eksternal, hanya pakai format PDF minimal.
     */
    public void saveToPDF() {
        String safeId = transactionId != null ? transactionId : "TRX-" + System.currentTimeMillis();

        // Pastikan folder receipts ada
        String directoryPath = "receipts";
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = "receipt_" + safeId + ".pdf";
        File outFile = new File(dir, fileName);
        this.pdfFilePath = outFile.getAbsolutePath();

        // Siapkan konten teks dari receipt
        String[] lines = generateReceiptText().split("\\r?\\n");

        // Build content stream (PDF drawing commands)
        StringBuilder content = new StringBuilder();
        content.append("BT\n");              // Begin Text
        content.append("/F1 12 Tf\n");       // Font F1 size 12
        content.append("72 800 Td\n");       // Position: x=72, y=800
        content.append("18 TL\n");           // Line spacing 14

        for (String line : lines) {
            content.append("(")
                   .append(escapePdfText(line))
                   .append(") Tj\n");       // Show text
            content.append("T*\n");         // Move to next line (using TL)
        }
        content.append("ET\n");             // End Text

        byte[] contentBytes = content.toString().getBytes(StandardCharsets.ISO_8859_1);
        int contentLength = contentBytes.length;

        try (FileOutputStream fos = new FileOutputStream(outFile)) {

            // Header PDF
            String header = "%PDF-1.4\n" +
                            "%\u00e2\u00e3\u00cf\u00d3\n";

            List<String> objects = new ArrayList<>();

            // 1: Catalog
            objects.add("1 0 obj\n" +
                       "<< /Type /Catalog /Pages 2 0 R >>\n" +
                       "endobj\n");

            // 2: Pages
            objects.add("2 0 obj\n" +
                       "<< /Type /Pages /Kids [3 0 R] /Count 1 >>\n" +
                       "endobj\n");

            // 3: Page
            objects.add("3 0 obj\n" +
                       "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842]\n" +
                       "   /Resources << /Font << /F1 4 0 R >> >>\n" +
                       "   /Contents 5 0 R >>\n" +
                       "endobj\n");

            // 4: Font
            objects.add("4 0 obj\n" +
                       "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\n" +
                       "endobj\n");

            // 5: Content stream
            StringBuilder obj5 = new StringBuilder();
            obj5.append("5 0 obj\n");
            obj5.append("<< /Length ").append(contentLength).append(" >>\n");
            obj5.append("stream\n");
            obj5.append(content);
            obj5.append("endstream\n");
            obj5.append("endobj\n");
            objects.add(obj5.toString());

            // Build full PDF + hitung offset untuk xref
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

            // XRef table
            StringBuilder xref = new StringBuilder();
            xref.append("xref\n");
            xref.append("0 ").append(objCount + 1).append("\n");
            xref.append("0000000000 65535 f \n");
            for (int off : offsets) {
                xref.append(String.format("%010d 00000 n \n", off));
            }

            // Trailer
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
