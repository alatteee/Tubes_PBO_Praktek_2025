package model;

import java.util.regex.Pattern;

/**
 * Merepresentasikan entitas Pelanggan (Customer).
 */
public class Customer {

    private final String customerId;
    private final String name;
    private final String phone;
    
    // Pattern untuk memvalidasi nomor telepon: Boleh diawali +62 atau 0, min 10 digit total.
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(?:\\+62|0)?\\d{9,15}$");

    /**
     * Konstruktor untuk membuat objek Customer.
     * @param customerId ID unik pelanggan.
     * @param name Nama pelanggan.
     * @param phone Nomor telepon pelanggan.
     */
    public Customer(String customerId, String name, String phone) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
    
    /**
     * Memeriksa apakah data Customer valid sesuai Business Rules (BR-05).
     * @return true jika nama dan nomor telepon valid.
     */
    public boolean isValid() {
        return isNameValid(this.name) && isPhoneValid(this.phone);
    }

    private boolean isNameValid(String name) {
        return name != null && !name.trim().isEmpty();
    }

    private boolean isPhoneValid(String phone) {
        if (phone == null) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + customerId + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}