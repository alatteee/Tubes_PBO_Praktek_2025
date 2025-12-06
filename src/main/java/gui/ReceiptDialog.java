package gui;

import model.Receipt;

import javax.swing.*;
import java.awt.*;

public class ReceiptDialog extends JDialog {

    private final Receipt receipt;
    private JTextArea txtReceipt;
    private JButton btnSave;
    private JButton btnClose;

    public ReceiptDialog(Frame owner, Receipt receipt) {
        super(owner, "Receipt", true);
        this.receipt = receipt;

        initComponents();
        initListeners();

        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout(8, 8));

        txtReceipt = new JTextArea(20, 60);
        txtReceipt.setEditable(false);
        txtReceipt.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtReceipt.setText(receipt.generateReceiptText());

        JScrollPane scrollPane = new JScrollPane(txtReceipt);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Save as PDF");
        btnClose = new JButton("Close");
        buttonPanel.add(btnSave);
        buttonPanel.add(btnClose);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initListeners() {
        btnSave.addActionListener(e -> {
            try {
                receipt.saveToPDF();
                JOptionPane.showMessageDialog(
                        this,
                        "Receipt saved to: " + receipt.getPdfFilePath(),
                        "Saved",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to save receipt: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnClose.addActionListener(e -> dispose());
    }
}
