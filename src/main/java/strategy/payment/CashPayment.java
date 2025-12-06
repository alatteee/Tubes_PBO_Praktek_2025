package strategy.payment;

public class CashPayment implements PaymentStrategy {

    @Override
    public String getName() {
        return "Cash";
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("[Payment] Cash payment processed: " + amount);
        return true;
    }
}
