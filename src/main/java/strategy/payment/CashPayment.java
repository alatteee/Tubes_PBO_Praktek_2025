package strategy.payment;

public class CashPayment implements PaymentStrategy {

    @Override
    public String getName() {
        return "Cash";
    }

    @Override
    public boolean pay(double amount) {
        // Untuk saat ini, kita anggap selalu berhasil
        // Nanti kalau mau, bisa ditambah logika (misal input jumlah uang dari user)
        System.out.println("[Payment] Cash payment processed: " + amount);
        return true;
    }
}
