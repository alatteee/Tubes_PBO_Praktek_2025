package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import facade.PetCareFacade;
import manager.CustomerManager;
import manager.PetManager;
import manager.OrderManager;
import model.Customer;
import model.Pet;
import model.ServiceOrder;
import model.Receipt;
import strategy.payment.CashPayment;
import strategy.payment.EWalletPayment;
import strategy.payment.PaymentStrategy;
import strategy.payment.TransferPayment;

public class MainFrame extends JFrame {

    private final PetCareFacade facade;

    // --- Komponen Customer/Pet tab ---
    private JTextField txtCustName;
    private JTextField txtCustPhone;
    private JTextField txtOwnerId;
    private JComboBox<String> cbPetType;
    private JTextField txtPetName;
    private JTextField txtPetAge;
    private JButton btnAddCustomer;
    private JButton btnAddPet;
    private JTable tblCustomers;
    private JTable tblPets;

    // --- Komponen Service Order tab ---
    private JTextField txtPetId;
    private JComboBox<String> cbServiceType;
    private JTextField txtEntry;
    private JTextField txtExit;
    private JButton btnCreateOrder;
    private JTable tblOrders;
    private JButton btnStart;
    private JButton btnFinish;

    // --- Komponen Checkout tab ---
    private JTable tblCheckoutOrders;
    private JComboBox<String> cbPayment;
    private JButton btnCheckout;

    private final DateTimeFormatter dtFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public MainFrame() {
        this.facade = new PetCareFacade();
        initGUI();
        initListeners();
        initialLoad();
    }

    private void initGUI() {
        setTitle("Pet Care System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Customer & Pet", createCustomerPetPanel());
        tabbedPane.addTab("Service Order", createServiceOrderPanel());
        tabbedPane.addTab("Checkout", createCheckoutPanel());

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
    }

    // ============================================================
    // =============== PANEL CUSTOMER & PET =======================
    // ============================================================
    private JPanel createCustomerPetPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // --- Customer form ---
        JPanel customerForm = new JPanel(new GridBagLayout());
        customerForm.setBorder(BorderFactory.createTitledBorder("Register Customer"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblCustName = new JLabel("Name:");
        txtCustName = new JTextField(20);
        JLabel lblCustPhone = new JLabel("Phone:");
        txtCustPhone = new JTextField(15);
        btnAddCustomer = new JButton("Add Customer");

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

        // --- Pet form ---
        JPanel petForm = new JPanel(new GridBagLayout());
        petForm.setBorder(BorderFactory.createTitledBorder("Register Pet"));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblOwnerId = new JLabel("Owner ID:");
        txtOwnerId = new JTextField(10);
        JLabel lblPetType = new JLabel("Type:");
        cbPetType = new JComboBox<>(new String[]{"Cat", "Dog", "Rabbit"});
        JLabel lblPetName = new JLabel("Pet Name:");
        txtPetName = new JTextField(15);
        JLabel lblPetAge = new JLabel("Age:");
        txtPetAge = new JTextField(5);
        btnAddPet = new JButton("Add Pet");

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

        JPanel topForms = new JPanel(new GridLayout(2, 1));
        topForms.add(customerForm);
        topForms.add(petForm);

        // --- Tables ---
        tblCustomers = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Customer ID", "Name", "Phone"}
        ));

        tblPets = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Pet ID", "Name", "Type", "Owner ID", "Status"}
        ));

        JSplitPane splitTables = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tblCustomers),
                new JScrollPane(tblPets)
        );
        splitTables.setResizeWeight(0.5);

        panel.add(topForms, BorderLayout.NORTH);
        panel.add(splitTables, BorderLayout.CENTER);
        return panel;
    }

    // ============================================================
    // =============== PANEL SERVICE ORDER ========================
    // ============================================================
    private JPanel createServiceOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel orderForm = new JPanel(new GridBagLayout());
        orderForm.setBorder(BorderFactory.createTitledBorder("Create Service Order"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblPetId = new JLabel("Pet ID:");
        txtPetId = new JTextField(10);
        JLabel lblServiceType = new JLabel("Service Type:");
        cbServiceType = new JComboBox<>(new String[]{"Grooming", "Boarding", "Medical"});
        JLabel lblEntry = new JLabel("Entry (yyyy-MM-dd HH:mm):");
        txtEntry = new JTextField(16);
        JLabel lblExit = new JLabel("Exit (for Boarding, optional):");
        txtExit = new JTextField(16);
        btnCreateOrder = new JButton("Create Order");

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

        tblOrders = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Order ID", "Pet", "Customer", "Service", "Status", "Total"}
        ));
        JScrollPane scrollOrders = new JScrollPane(tblOrders);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnStart = new JButton("Start Service");
        btnFinish = new JButton("Finish Service");
        statusPanel.add(btnStart);
        statusPanel.add(btnFinish);

        panel.add(orderForm, BorderLayout.NORTH);
        panel.add(scrollOrders, BorderLayout.CENTER);
        panel.add(statusPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ============================================================
    // ================== PANEL CHECKOUT ==========================
    // ============================================================
    private JPanel createCheckoutPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        tblCheckoutOrders = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Order ID", "Pet", "Customer", "Service", "Status", "Total"}
        ));
        JScrollPane scroll = new JScrollPane(tblCheckoutOrders);

        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Checkout"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblPayment = new JLabel("Payment Method:");
        cbPayment = new JComboBox<>(new String[]{"Cash", "Transfer Bank", "E-Wallet"});
        btnCheckout = new JButton("Pay & Print Receipt");

        gbc.gridx = 0; gbc.gridy = 0;
        bottomPanel.add(lblPayment, gbc);
        gbc.gridx = 1;
        bottomPanel.add(cbPayment, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        bottomPanel.add(btnCheckout, gbc);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ============================================================
    // =============== LISTENERS & HELPERS ========================
    // ============================================================
    private void initListeners() {
        // Add Customer
        btnAddCustomer.addActionListener(e -> {
            try {
                String name = txtCustName.getText().trim();
                String phone = txtCustPhone.getText().trim();
                facade.registerCustomer(name, phone);
                JOptionPane.showMessageDialog(this, "Customer registered");
                txtCustName.setText("");
                txtCustPhone.setText("");
                loadCustomers();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        // Add Pet
        btnAddPet.addActionListener(e -> {
            try {
                String ownerId = txtOwnerId.getText().trim();
                String type = (String) cbPetType.getSelectedItem();
                String petName = txtPetName.getText().trim();
                int age = Integer.parseInt(txtPetAge.getText().trim());

                facade.registerPet(ownerId, type, petName, age);
                JOptionPane.showMessageDialog(this, "Pet registered");
                txtPetName.setText("");
                txtPetAge.setText("");
                loadPets(); // nanti bisa khusus by owner
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Age harus angka", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                showError(ex);
            }
        });

        // Create Order
        btnCreateOrder.addActionListener(e -> {
            try {
                String petId = txtPetId.getText().trim();
                String serviceType = (String) cbServiceType.getSelectedItem();
                String entryText = txtEntry.getText().trim();
                String exitText = txtExit.getText().trim();

                LocalDateTime entry = LocalDateTime.parse(entryText, dtFormatter);
                LocalDateTime exit = null;
                if (!exitText.isBlank()) {
                    exit = LocalDateTime.parse(exitText, dtFormatter);
                }

                facade.createServiceOrder(petId, serviceType, entry, exit);
                JOptionPane.showMessageDialog(this, "Order created");
                loadActiveOrders();
                loadCheckoutOrders();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        // Start Service
        btnStart.addActionListener(e -> {
            int row = tblOrders.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih order dahulu", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String orderId = tblOrders.getValueAt(row, 0).toString();
            try {
                facade.startService(orderId);
                JOptionPane.showMessageDialog(this, "Service started");
                loadActiveOrders();
                loadCheckoutOrders();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        // Finish Service
        btnFinish.addActionListener(e -> {
            int row = tblOrders.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih order dahulu", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String orderId = tblOrders.getValueAt(row, 0).toString();
            try {
                facade.finishService(orderId);
                JOptionPane.showMessageDialog(this, "Service finished");
                loadActiveOrders();
                loadCheckoutOrders();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        // Checkout
        btnCheckout.addActionListener(e -> {
            int row = tblCheckoutOrders.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih order untuk checkout", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String orderId = tblCheckoutOrders.getValueAt(row, 0).toString();
            String method = (String) cbPayment.getSelectedItem();
            PaymentStrategy payment = createPaymentStrategy(method);

            try {
                Receipt receipt = facade.checkout(orderId, payment);
                JOptionPane.showMessageDialog(this, "Payment success");
                // Tampilkan dialog struk
                ReceiptDialog dialog = new ReceiptDialog(this, receipt);
                dialog.setVisible(true);

                loadActiveOrders();
                loadCheckoutOrders();
            } catch (Exception ex) {
                showError(ex);
            }
        });
    }

    private void initialLoad() {
        loadCustomers();
        loadPets();
        loadActiveOrders();
        loadCheckoutOrders();
    }

    // === Loader tabel (silakan sesuaikan dengan Manager/DAO kalian) ===
    private void loadCustomers() {
        try {
            CustomerManager cm = facade.getCustomerManager();
            List<Customer> customers = cm.getAllCustomers(); // TODO: pastikan method ini ada
            DefaultTableModel model = (DefaultTableModel) tblCustomers.getModel();
            model.setRowCount(0);
            for (Customer c : customers) {
                model.addRow(new Object[]{
                        c.getCustomerId(),
                        c.getName(),
                        c.getPhone()
                });
            }
        } catch (Exception e) {
            // boleh di-silent atau ditampilkan
            e.printStackTrace();
        }
    }

    private void loadPets() {
        try {
            PetManager pm = facade.getPetManager();
            List<Pet> pets = pm.getAllPets();
            DefaultTableModel model = (DefaultTableModel) tblPets.getModel();
            model.setRowCount(0);
            for (Pet p : pets) {
                model.addRow(new Object[]{
                        p.getPetId(),
                        p.getName(),
                        p.getClass().getSimpleName(),
                        // kolom "Owner ID" → langsung pakai ownerId dari Pet
                        p.getOwnerId(),
                        p.getStatus()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    

    private void loadActiveOrders() {
        try {
            List<ServiceOrder> orders = facade.getActiveOrders();
            DefaultTableModel model = (DefaultTableModel) tblOrders.getModel();
            model.setRowCount(0);
            for (ServiceOrder o : orders) {
                model.addRow(new Object[]{
                        o.getOrderId(),
                        o.getPet() != null ? o.getPet().getName() : "",
                        o.getCustomer() != null ? o.getCustomer().getName() : "",
                        o.getService() != null ? o.getService().getName() : "",
                        o.getStatus(),
                        o.getTotalCost()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCheckoutOrders() {
        try {
            // Sederhana: pakai getActiveOrders() lalu filter di facade/Manager,
            // atau kamu buat method khusus getFinishedOrders().
            OrderManager om = facade.getOrderManager();
            List<ServiceOrder> finished = om.getFinishedOrders(); // TODO: pastikan method ini ada / ganti
            DefaultTableModel model = (DefaultTableModel) tblCheckoutOrders.getModel();
            model.setRowCount(0);
            for (ServiceOrder o : finished) {
                model.addRow(new Object[]{
                        o.getOrderId(),
                        o.getPet() != null ? o.getPet().getName() : "",
                        o.getCustomer() != null ? o.getCustomer().getName() : "",
                        o.getService() != null ? o.getService().getName() : "",
                        o.getStatus(),
                        o.getTotalCost()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PaymentStrategy createPaymentStrategy(String method) {
        if (method == null) return new CashPayment(); // default
        String m = method.toLowerCase();
        if (m.contains("transfer")) {
            return new TransferPayment();
        } else if (m.contains("wallet") || m.contains("e-wallet") || m.contains("ewallet")) {
            return new EWalletPayment();
        } else {
            return new CashPayment();
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {}

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
