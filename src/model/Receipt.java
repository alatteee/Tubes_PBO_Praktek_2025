package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import strategy.payment.PaymentStrategy; // aktifkan kalau pakai package
// import model.ServiceOrder; // aktifkan kalau ServiceOrder pakai package

public class Receipt {

    private String transactionId;
    private LocalDateTime transactionTime;
    private ServiceOrder order;
    private PaymentStrategy payment;
    private String pdfFilePath;

    public Receipt(String transactionId,
                   LocalDateTime transactionTime,
                   ServiceOrder order,
                   PaymentStrategy payment) {
        this.transactionId = transactionId;
        this.transactionTime = transactionTime;
        this.order = order;
        this.payment = payment;
    }

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
                sb.append("Service        : ")
                  .append(order.getService().getName())
                  .append("\n");
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
     * Nanti kalau mau pakai library PDF beneran bisa diganti di sini.
     */
    public void saveToPDF(String directoryPath) throws IOException {
        if (directoryPath == null || directoryPath.isBlank()) {
            directoryPath = "."; // current directory
        }

        String safeId = transactionId != null ? transactionId : "TRX-" + System.currentTimeMillis();
        String fileName = "receipt_" + safeId + ".pdf"; // ekstensi .pdf walau isinya text
        String fullPath = directoryPath + "/" + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath))) {
            writer.write(generateReceiptText());
        }

        this.pdfFilePath = fullPath;
    }
}
