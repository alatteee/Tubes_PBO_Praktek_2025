package gui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import model.Receipt;

public class ReceiptDialog extends JDialog {

    private Receipt receipt;
    private JTextArea textArea;

    public ReceiptDialog(Frame parent, Receipt receipt) {
        super(parent, "Receipt", true);
        this.receipt = receipt;
        initDialog();
    }

    private void initDialog() {
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        if (receipt != null) {
            textArea.setText(receipt.generateReceiptText());
        }
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save as PDF");
        JButton btnClose = new JButton("Close");

        bottomPanel.add(btnSave);
        bottomPanel.add(btnClose);

        add(bottomPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> onSave());
        btnClose.addActionListener(e -> dispose());
    }

    private void onSave() {
        if (receipt == null) {
            JOptionPane.showMessageDialog(this,
                    "No receipt data to save.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose folder to save receipt");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String dir = chooser.getSelectedFile().getAbsolutePath();
            try {
                receipt.saveToPDF(dir);
                JOptionPane.showMessageDialog(this,
                        "Receipt saved to: " + receipt.getPdfFilePath(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Failed to save receipt: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
