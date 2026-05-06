package com.hospital.ui.panels;

import com.hospital.controller.PatientController;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PatientProfilePanel extends JPanel {
    private final JTextField patientIdField = styledField();
    private final JLabel fullNameValue = styledValue();
    private final JLabel dobValue = styledValue();
    private final JLabel genderValue = styledValue();
    private final JLabel contactValue = styledValue();
    private final JLabel addressValue = styledValue();
    private final JLabel emergencyNameValue = styledValue();
    private final JLabel emergencyPhoneValue = styledValue();
    private final JLabel bloodValue = styledValue();
    private final JLabel allergiesValue = styledValue();
    private final JLabel registrationValue = styledValue();
    private final JLabel updatedValue = styledValue();

    public PatientProfilePanel(User user) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_BOTTOM);

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 18, 8, 18));

        JLabel title = new JLabel("Patient Profile");
        title.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 22f));
        title.setForeground(UITheme.PRIMARY_DARK);

        JLabel subtitle = new JLabel("Load a patient record by ID to view full profile details.");
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

    private JComponent createBody() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 18, 18, 18));

        JPanel card = new RoundedCard();
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JPanel lookup = new JPanel(new BorderLayout(10, 0));
        lookup.setOpaque(false);
        patientIdField.setPreferredSize(new Dimension(260, 38));

        JButton loadButton = styledButton("Load Profile", UITheme.PRIMARY);
        JButton clearButton = styledButton("Clear", UITheme.SECONDARY);
        loadButton.setPreferredSize(new Dimension(140, 40));
        clearButton.setPreferredSize(new Dimension(120, 40));

        JPanel lookupButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        lookupButtons.setOpaque(false);
        lookupButtons.add(clearButton);
        lookupButtons.add(loadButton);

        lookup.add(patientIdField, BorderLayout.CENTER);
        lookup.add(lookupButtons, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(sectionLabel("Patient ID"), gbc);
        gbc.gridy = 1;
        card.add(lookup, gbc);

        int row = 2;
        row = addRow(card, gbc, row, "Full Name", fullNameValue);
        row = addRow(card, gbc, row, "Date of Birth", dobValue);
        row = addRow(card, gbc, row, "Gender", genderValue);
        row = addRow(card, gbc, row, "Contact Number", contactValue);
        row = addRow(card, gbc, row, "Address", addressValue);
        row = addRow(card, gbc, row, "Emergency Contact Name", emergencyNameValue);
        row = addRow(card, gbc, row, "Emergency Contact Phone", emergencyPhoneValue);
        row = addRow(card, gbc, row, "Blood Group", bloodValue);
        row = addRow(card, gbc, row, "Known Allergies", allergiesValue);
        row = addRow(card, gbc, row, "Registration Date", registrationValue);
        addRow(card, gbc, row, "Last Updated", updatedValue);

        loadButton.addActionListener(e -> loadProfile());
        clearButton.addActionListener(e -> clearProfile());
        patientIdField.addActionListener(e -> loadProfile());

        wrapper.add(card, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private void loadProfile() {
        try {
            String id = patientIdField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a patient ID.", "Profile", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Patient patient = new PatientController().getById(id);
            if (patient == null) {
                JOptionPane.showMessageDialog(this, "No patient found for ID: " + id, "Profile", JOptionPane.INFORMATION_MESSAGE);
                clearProfile();
                return;
            }

            fullNameValue.setText(valueOrDash(patient.getFullName()));
            dobValue.setText(patient.getDateOfBirth() != null ? patient.getDateOfBirth().toString() : "-");
            genderValue.setText(valueOrDash(patient.getGender()));
            contactValue.setText(valueOrDash(patient.getContactNumber()));
            addressValue.setText(valueOrDash(patient.getAddress()));
            emergencyNameValue.setText(valueOrDash(patient.getEmergencyContactName()));
            emergencyPhoneValue.setText(valueOrDash(patient.getEmergencyContactPhone()));
            bloodValue.setText(valueOrDash(patient.getBloodGroup()));
            allergiesValue.setText(valueOrDash(patient.getAllergies()));
            registrationValue.setText(patient.getRegistrationDate() != null ? patient.getRegistrationDate().toString() : "-");
            updatedValue.setText(patient.getLastUpdated() != null ? patient.getLastUpdated().toString() : "-");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Profile Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearProfile() {
        patientIdField.setText("");
        fullNameValue.setText("-");
        dobValue.setText("-");
        genderValue.setText("-");
        contactValue.setText("-");
        addressValue.setText("-");
        emergencyNameValue.setText("-");
        emergencyPhoneValue.setText("-");
        bloodValue.setText("-");
        allergiesValue.setText("-");
        registrationValue.setText("-");
        updatedValue.setText("-");
    }

    private int addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JLabel value) {
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        panel.add(sectionLabel(label), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.65;
        panel.add(value, gbc);

        return row + 1;
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

    private JLabel styledValue() {
        JLabel label = new JLabel("-");
        label.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        label.setForeground(new Color(50, 50, 50));
        label.setOpaque(true);
        label.setBackground(new Color(250, 252, 255));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 230), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return label;
    }

    private JButton styledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
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

    private String valueOrDash(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
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
