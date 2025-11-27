package strategy.payment;

public class TransferPayment implements PaymentStrategy {

    private String bankName;
    private String accountNumber;

    public TransferPayment() {
        // nilai default; nanti kalau perlu bisa dikembangkan
        this.bankName = "Bank Default";
        this.accountNumber = "0000000000";
    }

    public TransferPayment(String bankName, String accountNumber) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }

    @Override
    public String getName() {
        return "Transfer Bank";
    }

    @Override
    public boolean pay(double amount) {
        // Dummy: dianggap selalu berhasil
        System.out.println("[Payment] Transfer to " + bankName +
                " (" + accountNumber + ") amount: " + amount);
        return true;
    }

    // Getter / Setter opsional

    public String getBankName() {
        return bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
