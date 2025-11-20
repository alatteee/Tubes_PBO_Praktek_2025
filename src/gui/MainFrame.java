package gui;

import javax.swing.*;
import java.awt.*;

import facade.PetCareFacade;

public class MainFrame extends JFrame {

    private PetCareFacade facade;

    public MainFrame() {
        this.facade = new PetCareFacade();
        initGUI();
    }

    private void initGUI() {
        setTitle("Pet Care System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null); // center

        // Untuk sementara, layout sederhana dulu.
        // Nanti kamu bisa ganti dengan JTabbedPane: Customer/Pet, Service, Checkout.
        setLayout(new BorderLayout());

        JLabel placeholder = new JLabel("Pet Care System - GUI coming soon...", SwingConstants.CENTER);
        add(placeholder, BorderLayout.CENTER);
    }

    public PetCareFacade getFacade() {
        return facade;
    }

    public static void main(String[] args) {
        // Opsional: set Look and Feel ke sistem
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {}

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
