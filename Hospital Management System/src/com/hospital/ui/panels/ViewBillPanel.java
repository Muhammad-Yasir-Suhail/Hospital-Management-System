package com.hospital.ui.panels;

import com.hospital.controller.BillingController;
import com.hospital.model.Bill;
import com.hospital.model.User;
import com.hospital.utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.print.PrinterException;

public class ViewBillPanel extends JPanel {
    private final JTextArea receipt = new JTextArea(20, 60);
    private final JTextField billIdField = styledField();

    public ViewBillPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_BOTTOM);

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(user), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 18, 8, 18));

        JLabel title = new JLabel("View Bill");
        title.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 22f));
        title.setForeground(UITheme.PRIMARY_DARK);

        JLabel subtitle = new JLabel("Load a bill by ID, print receipt, or update payment status.");
        subtitle.setFont(UITheme.BODY_FONT);
        subtitle.setForeground(new Color(90, 90, 90));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title);
        text.add(Box.createVerticalStrut(4));
        text.add(subtitle);

        header.add(text, BorderLayout.WEST);
        return header;
    }

    private JComponent createBody(User user) {
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(0, 18, 18, 18));

        JPanel topCard = new RoundedCard();
        topCard.setLayout(new GridBagLayout());
        topCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        JButton loadButton = styledButton("Load Bill", UITheme.PRIMARY);
        JButton printButton = styledButton("Print Bill", UITheme.SECONDARY);
        JButton markPaidButton = styledButton("Mark as Paid", UITheme.SUCCESS);

        loadButton.setPreferredSize(new Dimension(120, 40));
        printButton.setPreferredSize(new Dimension(120, 40));
        markPaidButton.setPreferredSize(new Dimension(140, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        topCard.add(sectionLabel("Bill ID"), gbc);

        gbc.gridy = 1;
        topCard.add(billIdField, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(loadButton);
        buttons.add(printButton);
        buttons.add(markPaidButton);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0;
        topCard.add(buttons, gbc);

        receipt.setEditable(false);
        receipt.setFont(new Font("Consolas", Font.PLAIN, 14));
        receipt.setBorder(new EmptyBorder(14, 14, 14, 14));
        receipt.setBackground(new Color(252, 253, 255));
        receipt.setForeground(new Color(35, 35, 35));

        JScrollPane receiptScroll = new JScrollPane(receipt);
        receiptScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 230), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        receiptScroll.getVerticalScrollBar().setUnitIncrement(16);

        body.add(topCard, BorderLayout.NORTH);
        body.add(receiptScroll, BorderLayout.CENTER);

        loadButton.addActionListener(e -> loadBill());
        billIdField.addActionListener(e -> loadBill());
        printButton.addActionListener(e -> printReceipt());
        markPaidButton.addActionListener(e -> markAsPaid(user));

        return body;
    }

    private void loadBill() {
        try {
            String billId = billIdField.getText().trim();
            if (billId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a bill ID.", "View Bill", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Bill bill = new BillingController().getById(billId);
            if (bill == null) {
                JOptionPane.showMessageDialog(this, "Bill not found.", "View Bill", JOptionPane.INFORMATION_MESSAGE);
                receipt.setText("");
                return;
            }
            receipt.setText(format(bill));
            receipt.setCaretPosition(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "View Bill", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printReceipt() {
        try {
            if (receipt.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Load a bill before printing.", "View Bill", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            receipt.print();
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markAsPaid(User user) {
        try {
            String billId = billIdField.getText().trim();
            if (billId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a bill ID first.", "View Bill", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new BillingController().markPaid(billId, user.getUsername());
            JOptionPane.showMessageDialog(this, "Payment status updated.", "View Bill", JOptionPane.INFORMATION_MESSAGE);
            loadBill();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "View Bill", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String format(Bill b) {
        return "City Hospital\n"
                + "----------------------------------------\n"
                + "Bill ID: " + b.getBillId() + "\n"
                + "Date: " + b.getBillDate() + "\n\n"
                + "Patient: " + b.getPatientName() + " (" + b.getPatientId() + ")\n"
                + "Contact: " + b.getContactNumber() + "\n\n"
                + "Consultation: " + b.getConsultationFee() + "\n"
                + "Medication:   " + b.getMedicationCharges() + "\n"
                + "Tests:        " + b.getTestCharges() + "\n"
                + "Other:        " + b.getOtherCharges() + "\n"
                + "----------------------------------------\n"
                + "Total: " + b.getTotalAmount() + "\n\n"
                + "Status: " + b.getPaymentStatus();
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.BODY_FONT.deriveFont(Font.BOLD, 13f));
        label.setForeground(UITheme.PRIMARY_DARK);
        return label;
    }

    private JTextField styledField() {
        JTextField field = new JTextField();
        field.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 230), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(new Color(250, 252, 255));
        return field;
    }

    private JButton styledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), UITheme.BUTTON_RADIUS, UITheme.BUTTON_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(UITheme.BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return button;
    }

    private static class RoundedCard extends JPanel {
        RoundedCard() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            g2.setColor(new Color(0, 0, 0, 24));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
