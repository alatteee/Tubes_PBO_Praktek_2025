package model;

import java.util.regex.Pattern;

/**
 * Merepresentasikan entitas Pelanggan (Customer).
 * Menerapkan Business Rules:
 * - Nama tidak boleh kosong
 * - Nomor telepon minimal 10 digit, angka atau boleh awalan +62
 */
public class Customer {

    private final String customerId;
    private final String name;
    private final String phone;
    
    // Regex: boleh +62 atau 0 di depan, minimal 10 digit total
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(?:\\+62|0)?\\d{9,15}$");

    /**
     * Konstruktor Customer dengan validasi langsung.
     * Jika data tidak valid → lempar IllegalArgumentException.
     */
    public Customer(String customerId, String name, String phone) {

        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID tidak boleh kosong.");
        }

        if (!isNameValid(name)) {
            throw new IllegalArgumentException("Nama customer tidak boleh kosong.");
        }

        if (!isPhoneValid(phone)) {
            throw new IllegalArgumentException("Nomor telepon tidak valid: " + phone);
        }

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
     * Validasi untuk testing tambahan.
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