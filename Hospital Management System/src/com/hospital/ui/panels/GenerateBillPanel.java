package com.hospital.ui.panels;

import com.hospital.controller.BillingController;
import com.hospital.model.Bill;
import com.hospital.model.User;
import com.hospital.utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;

public class GenerateBillPanel extends JPanel {
    private final JTextField patientIdField = styledField();
    private final JTextField appointmentIdField = styledField();
    private final JTextField consultationField = styledField("0");
    private final JTextField medicationField = styledField("0");
    private final JTextField testField = styledField("0");
    private final JTextField otherField = styledField("0");
    private final JLabel totalValue = styledTotalLabel();
    private final JComboBox<String> methodCombo = styledCombo(new String[]{"Cash", "Card", "Insurance"});
    private final JComboBox<String> statusCombo = styledCombo(new String[]{"Paid", "Unpaid"});

    public GenerateBillPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_BOTTOM);

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(user), BorderLayout.CENTER);
        attachTotalCalculation();
        recalculateTotal();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 18, 8, 18));

        JLabel title = new JLabel("Generate Bill");
        title.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 22f));
        title.setForeground(UITheme.PRIMARY_DARK);

        JLabel subtitle = new JLabel("Create patient bills, calculate totals automatically, and save payment details.");
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
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 18, 18, 18));

        JPanel card = new RoundedCard();
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton saveButton = styledButton("Save Bill", UITheme.SUCCESS);
        JButton clearButton = styledButton("Clear", UITheme.SECONDARY);
        saveButton.setPreferredSize(new Dimension(130, 42));
        clearButton.setPreferredSize(new Dimension(110, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;
        row = addField(card, gbc, row, "Patient ID*", patientIdField);
        row = addField(card, gbc, row, "Linked Appointment", appointmentIdField);
        row = addField(card, gbc, row, "Consultation Fee", consultationField);
        row = addField(card, gbc, row, "Medication Charges", medicationField);
        row = addField(card, gbc, row, "Test/Lab Charges", testField);
        row = addField(card, gbc, row, "Other Charges", otherField);
        row = addField(card, gbc, row, "Total Amount", totalValue);
        row = addField(card, gbc, row, "Payment Method", methodCombo);
        row = addField(card, gbc, row, "Payment Status", statusCombo);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(clearButton);
        buttonRow.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        card.add(buttonRow, gbc);

        saveButton.addActionListener(e -> saveBill(user));
        clearButton.addActionListener(e -> clearForm());

        wrapper.add(card, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private void attachTotalCalculation() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                recalculateTotal();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                recalculateTotal();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                recalculateTotal();
            }
        };
        consultationField.getDocument().addDocumentListener(listener);
        medicationField.getDocument().addDocumentListener(listener);
        testField.getDocument().addDocumentListener(listener);
        otherField.getDocument().addDocumentListener(listener);
    }

    private void recalculateTotal() {
        try {
            BigDecimal total = new BigDecimal(consultationField.getText().trim())
                    .add(new BigDecimal(medicationField.getText().trim()))
                    .add(new BigDecimal(testField.getText().trim()))
                    .add(new BigDecimal(otherField.getText().trim()));
            totalValue.setText(total.toString());
        } catch (Exception ignored) {
            totalValue.setText("0.00");
        }
    }

    private void saveBill(User user) {
        try {
            String patientId = patientIdField.getText().trim();
            if (patientId.isEmpty()) {
                throw new IllegalArgumentException("Patient ID is required.");
            }

            Bill bill = new Bill();
            bill.setPatientId(patientId);
            String appointmentId = appointmentIdField.getText().trim();
            bill.setAppointmentId(appointmentId.isEmpty() ? null : appointmentId);
            bill.setConsultationFee(new BigDecimal(consultationField.getText().trim()));
            bill.setMedicationCharges(new BigDecimal(medicationField.getText().trim()));
            bill.setTestCharges(new BigDecimal(testField.getText().trim()));
            bill.setOtherCharges(new BigDecimal(otherField.getText().trim()));
            bill.setTotalAmount(new BigDecimal(totalValue.getText().trim()));
            bill.setPaymentMethod((String) methodCombo.getSelectedItem());
            bill.setPaymentStatus((String) statusCombo.getSelectedItem());

            String id = new BillingController().save(bill, user.getUsername());
            JOptionPane.showMessageDialog(this, "Bill saved successfully: " + id, "Generate Bill", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Generate Bill", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        patientIdField.setText("");
        appointmentIdField.setText("");
        consultationField.setText("0");
        medicationField.setText("0");
        testField.setText("0");
        otherField.setText("0");
        methodCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
        recalculateTotal();
    }

    private int addField(JPanel panel, GridBagConstraints gbc, int row, String label, Component field) {
        JLabel jLabel = sectionLabel(label);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        panel.add(jLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.65;
        panel.add(field, gbc);

        return row + 1;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.BODY_FONT.deriveFont(Font.BOLD, 13f));
        label.setForeground(UITheme.PRIMARY_DARK);
        return label;
    }

    private JTextField styledField() {
        return styledField("");
    }

    private JTextField styledField(String value) {
        JTextField field = new JTextField(value);
        field.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 230), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(new Color(250, 252, 255));
        return field;
    }

    private JLabel styledTotalLabel() {
        JLabel label = new JLabel("0.00");
        label.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 24f));
        label.setForeground(UITheme.PRIMARY_DARK);
        return label;
    }

    private JComboBox<String> styledCombo(String[] values) {
        JComboBox<String> combo = new JComboBox<>(values);
        combo.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        combo.setBackground(Color.WHITE);
        return combo;
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
