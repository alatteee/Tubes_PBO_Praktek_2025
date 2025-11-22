package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import strategy.payment.PaymentStrategy;

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

        sb.append("=========== PET CARE SYSTEM RECEIPT ===========\n");
        sb.append("Transaction ID : ").append(transactionId).append("\n");
        sb.append("Date/Time      : ").append(dtf.format(transactionTime)).append("\n\n");

        if (order != null) {
            sb.append("Order ID       : ").append(order.getOrderId()).append("\n");

            if (order.getCustomer() != null) {
                sb.append("Customer       : ")
                  .append(order.getCustomer().getName())
                  .append(" (")
                  .append(order.getCustomer().getCustomerId())
                  .append(")\n");
            }

            if (order.getPet() != null) {
                sb.append("Pet            : ")
                  .append(order.getPet().getName())
                  .append(" (")
                  .append(order.getPet().getClass().getSimpleName())
                  .append(")\n");
            }

            if (order.getService() != null) {
                sb.append("Service        : ").append(order.getService().getName()).append("\n");
            }

            sb.append("Total Cost     : ").append(order.getTotalCost()).append("\n");
        }

        if (payment != null) {
            sb.append("Payment Method : ").append(payment.getName()).append("\n");
        }

        sb.append("===============================================\n");
        sb.append("   Thank you for using our Pet Care Service!   \n");
        sb.append("===============================================\n");

        return sb.toString();
    }

    /**
     * Dummy saveToPDF: sebenarnya menyimpan file TXT, tapi ekstensi .pdf.
     * Sesuai spec: TIDAK ADA PARAMETER.
     */
    public void saveToPDF() {
        String safeId = transactionId != null ? transactionId : "TRX-" + System.currentTimeMillis();
        String directoryPath = "receipts";

        String fileName = "receipt_" + safeId + ".pdf";
        String fullPath = directoryPath + "/" + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath))) {
            writer.write(generateReceiptText());
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan receipt ke file: " + fullPath, e);
        }

        this.pdfFilePath = fullPath;
    }
}
