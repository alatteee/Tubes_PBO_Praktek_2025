package strategy.payment; 

public interface PaymentStrategy {

    String getName();

    // Melakukan proses pembayaran.
    @param amount 
    @return 
    boolean pay(double amount);
}
