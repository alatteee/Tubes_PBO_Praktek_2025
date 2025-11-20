package gui;

import javax.swing.*;
import java.awt.*;

import facade.PetCareFacade;

public class MainFrame extends JFrame {

    private PetCareFacade facade;

    private JTabbedPane tabbedPane;

    // Nanti kita tambahin field komponen lain (tabel, textfield, dll) kalau sudah masuk fase wiring
    public MainFrame() {
        this.facade = new PetCareFacade();
        initGUI();
    }

    private void initGUI() {
        setTitle("Pet Care System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Customer & Pet", createCustomerPetPanel());
        tabbedPane.addTab("Service Order", createServiceOrderPanel());
        tabbedPane.addTab("Checkout", createCheckoutPanel());

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Panel untuk data Customer dan Pet.
     * Sekarang fokus layout dulu, event ke facade nanti.
     */
    private JPanel createCustomerPetPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel form customer di atas
        JPanel customerForm = new JPanel(new GridBagLayout());
        customerForm.setBorder(BorderFactory.createTitledBorder("Register Customer"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblCustName = new JLabel("Name:");
        JTextField txtCustName = new JTextField(20);
        JLabel lblCustPhone = new JLabel("Phone:");
        JTextField txtCustPhone = new JTextField(15);
        JButton btnAddCustomer = new JButton("Add Customer");

        gbc.gridx = 0; gbc.gridy = 0;
        customerForm.add(lblCustName, gbc);
        gbc.gridx = 1;
        customerForm.add(txtCustName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        customerForm.add(lblCustPhone, gbc);
        gbc.gridx = 1;
        customerForm.add(txtCustPhone, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        customerForm.add(btnAddCustomer, gbc);

        // Panel form pet di tengah
        JPanel petForm = new JPanel(new GridBagLayout());
        petForm.setBorder(BorderFactory.createTitledBorder("Register Pet"));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblOwnerId = new JLabel("Owner ID:");
        JTextField txtOwnerId = new JTextField(10); // nanti bisa diganti combo/table
        JLabel lblPetType = new JLabel("Type:");
        JComboBox<String> cbPetType = new JComboBox<>(new String[]{"Cat", "Dog", "Rabbit"});
        JLabel lblPetName = new JLabel("Name:");
        JTextField txtPetName = new JTextField(15);
        JLabel lblPetAge = new JLabel("Age:");
        JTextField txtPetAge = new JTextField(5);
        JButton btnAddPet = new JButton("Add Pet");

        gbc.gridx = 0; gbc.gridy = 0;
        petForm.add(lblOwnerId, gbc);
        gbc.gridx = 1;
        petForm.add(txtOwnerId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        petForm.add(lblPetType, gbc);
        gbc.gridx = 1;
        petForm.add(cbPetType, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        petForm.add(lblPetName, gbc);
        gbc.gridx = 1;
        petForm.add(txtPetName, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        petForm.add(lblPetAge, gbc);
        gbc.gridx = 1;
        petForm.add(txtPetAge, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        petForm.add(btnAddPet, gbc);

        // Tabel customer & pet di bawah (sementara dummy model)
        JTable tblCustomers = new JTable(
                new Object[][]{},
                new String[]{"Customer ID", "Name", "Phone"}
        );
        JTable tblPets = new JTable(
                new Object[][]{},
                new String[]{"Pet ID", "Name", "Type", "Owner ID", "Status"}
        );

        JSplitPane splitTables = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tblCustomers),
                new JScrollPane(tblPets)
        );
        splitTables.setResizeWeight(0.5);

        // Susun di panel utama
        JPanel topForms = new JPanel(new GridLayout(2, 1));
        topForms.add(customerForm);
        topForms.add(petForm);

        panel.add(topForms, BorderLayout.NORTH);
        panel.add(splitTables, BorderLayout.CENTER);

        // NOTE:
        // ActionListener tombol (btnAddCustomer, btnAddPet) nanti ditambah setelah Facade & Manager siap.
        return panel;
    }

    /**
     * Panel untuk membuat dan mengelola service order.
     */
    private JPanel createServiceOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel orderForm = new JPanel(new GridBagLayout());
        orderForm.setBorder(BorderFactory.createTitledBorder("Create Service Order"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblPetId = new JLabel("Pet ID:");
        JTextField txtPetId = new JTextField(10);
        JLabel lblServiceType = new JLabel("Service Type:");
        JComboBox<String> cbServiceType = new JComboBox<>(new String[]{"Grooming", "Boarding", "Medical"});

        JLabel lblEntry = new JLabel("Entry Time (yyyy-MM-dd HH:mm):");
        JTextField txtEntry = new JTextField(16);
        JLabel lblExit = new JLabel("Exit Time (optional / boarding):");
        JTextField txtExit = new JTextField(16);

        JButton btnCreateOrder = new JButton("Create Order");

        gbc.gridx = 0; gbc.gridy = 0;
        orderForm.add(lblPetId, gbc);
        gbc.gridx = 1;
        orderForm.add(txtPetId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        orderForm.add(lblServiceType, gbc);
        gbc.gridx = 1;
        orderForm.add(cbServiceType, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        orderForm.add(lblEntry, gbc);
        gbc.gridx = 1;
        orderForm.add(txtEntry, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        orderForm.add(lblExit, gbc);
        gbc.gridx = 1;
        orderForm.add(txtExit, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        orderForm.add(btnCreateOrder, gbc);

        // Tabel orders aktif
        JTable tblOrders = new JTable(
                new Object[][]{},
                new String[]{"Order ID", "Pet", "Customer", "Service", "Status", "Total"}
        );

        JScrollPane scrollOrders = new JScrollPane(tblOrders);

        // Panel tombol status (Start/Finish)
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnStart = new JButton("Start Service");
        JButton btnFinish = new JButton("Finish Service");
        statusPanel.add(btnStart);
        statusPanel.add(btnFinish);

        panel.add(orderForm, BorderLayout.NORTH);
        panel.add(scrollOrders, BorderLayout.CENTER);
        panel.add(statusPanel, BorderLayout.SOUTH);

        // ActionListener order & tombol status nanti kita isi setelah Facade & Manager siap.
        return panel;
    }

    /**
     * Panel untuk Checkout dan membuka ReceiptDialog.
     */
    private JPanel createCheckoutPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabel order yang sudah "Selesai" dan siap di-checkout
        JTable tblCheckoutOrders = new JTable(
                new Object[][]{},
                new String[]{"Order ID", "Pet", "Customer", "Service", "Status", "Total"}
        );

        JScrollPane scroll = new JScrollPane(tblCheckoutOrders);

        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Checkout"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblPayment = new JLabel("Payment Method:");
        JComboBox<String> cbPayment = new JComboBox<>(new String[]{"Cash", "Transfer Bank", "E-Wallet"});
        JButton btnCheckout = new JButton("Pay & Print Receipt");

        gbc.gridx = 0; gbc.gridy = 0;
        bottomPanel.add(lblPayment, gbc);
        gbc.gridx = 1;
        bottomPanel.add(cbPayment, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        bottomPanel.add(btnCheckout, gbc);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Nanti: btnCheckout akan ambil order terpilih, buat PaymentStrategy, panggil facade.checkout(...),
        // lalu buka ReceiptDialog.
        return panel;
    }

    public PetCareFacade getFacade() {
        return facade;
    }

    public static void main(String[] args) {
        // Opsional: pakai Look & Feel OS
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {}

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
