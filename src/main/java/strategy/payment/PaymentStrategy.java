//paymentSTrategy.java
package strategy.payment; 

public interface PaymentStrategy {

    String getName();

    /**
     * Melakukan proses pembayaran.
     * @param amount jumlah yang harus dibayar
     * @return true jika pembayaran dianggap berhasil
     */
    boolean pay(double amount);
}