package strategy.payment;

public class EWalletPayment implements PaymentStrategy {

    private String providerName;
    private String walletId;

    public EWalletPayment() {
        this.providerName = "Default E-Wallet";
        this.walletId = "user@example.com";
    }

    public EWalletPayment(String providerName, String walletId) {
        this.providerName = providerName;
        this.walletId = walletId;
    }

    @Override
    public String getName() {
        return "E-Wallet";
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("[Payment] E-Wallet " + providerName +
                " (" + walletId + ") amount: " + amount);
        return true;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getWalletId() {
        return walletId;
    }
}
