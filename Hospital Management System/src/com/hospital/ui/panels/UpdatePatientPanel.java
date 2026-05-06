package com.hospital.ui.panels;

import com.hospital.controller.PatientController;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UpdatePatientPanel extends JPanel {
    private final JTextField patientIdField = styledField();
    private final JTextField fullNameField = styledField();
    private final JTextField dobField = styledField();
    private final JComboBox<String> genderField = styledCombo(new String[]{"Male", "Female", "Other"});
    private final JTextField contactField = styledField();
    private final JTextArea addressField = styledArea();
    private final JTextField emergencyNameField = styledField();
    private final JTextField emergencyPhoneField = styledField();
    private final JComboBox<String> bloodField = styledCombo(new String[]{"", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
    private final JTextArea allergiesField = styledArea();

    public UpdatePatientPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_BOTTOM);

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(user), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 18, 8, 18));

        JLabel title = new JLabel("Update Patient");
        title.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 22f));
        title.setForeground(UITheme.PRIMARY_DARK);

        JLabel subtitle = new JLabel("Load a record, edit the fields, and save the updated patient profile.");
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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JPanel lookup = new JPanel(new BorderLayout(10, 0));
        lookup.setOpaque(false);
        patientIdField.setPreferredSize(new Dimension(260, 38));

        JButton loadButton = styledButton("Load Patient", UITheme.PRIMARY);
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
        row = addField(card, gbc, row, "Full Name", fullNameField);
        row = addField(card, gbc, row, "Date of Birth (DD/MM/YYYY)", dobField);
        row = addField(card, gbc, row, "Gender", genderField);
        row = addField(card, gbc, row, "Contact Number", contactField);
        row = addField(card, gbc, row, "Address", new JScrollPane(addressField));
        row = addField(card, gbc, row, "Emergency Contact Name", emergencyNameField);
        row = addField(card, gbc, row, "Emergency Contact Phone", emergencyPhoneField);
        row = addField(card, gbc, row, "Blood Group", bloodField);
        row = addField(card, gbc, row, "Known Allergies", new JScrollPane(allergiesField));

        JButton saveButton = styledButton("Save Changes", UITheme.SUCCESS);
        saveButton.setPreferredSize(new Dimension(170, 42));

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionRow.setOpaque(false);
        actionRow.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        card.add(actionRow, gbc);

        loadButton.addActionListener(e -> loadPatient());
        clearButton.addActionListener(e -> clearForm());
        saveButton.addActionListener(e -> savePatient(user));
        patientIdField.addActionListener(e -> loadPatient());

        wrapper.add(card, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private void loadPatient() {
        try {
            String id = patientIdField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a patient ID.", "Update Patient", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Patient patient = new PatientController().getById(id);
            if (patient == null) {
                JOptionPane.showMessageDialog(this, "No patient found for ID: " + id, "Update Patient", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                patientIdField.setText(id);
                return;
            }

            patientIdField.setText(patient.getPatientId());
            fullNameField.setText(valueOrEmpty(patient.getFullName()));
            dobField.setText(patient.getDateOfBirth() != null ? patient.getDateOfBirth().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
            genderField.setSelectedItem(patient.getGender());
            contactField.setText(valueOrEmpty(patient.getContactNumber()));
            addressField.setText(valueOrEmpty(patient.getAddress()));
            emergencyNameField.setText(valueOrEmpty(patient.getEmergencyContactName()));
            emergencyPhoneField.setText(valueOrEmpty(patient.getEmergencyContactPhone()));
            bloodField.setSelectedItem(patient.getBloodGroup() == null ? "" : patient.getBloodGroup());
            allergiesField.setText(valueOrEmpty(patient.getAllergies()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void savePatient(User user) {
        try {
            String id = patientIdField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Load a patient before saving.", "Update Patient", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Patient patient = new Patient();
            patient.setPatientId(id);
            patient.setFullName(fullNameField.getText().trim());
            patient.setDateOfBirth(new PatientController().parseDate(dobField.getText().trim()));
            patient.setGender((String) genderField.getSelectedItem());
            patient.setContactNumber(contactField.getText().trim());
            patient.setAddress(addressField.getText().trim());
            patient.setEmergencyContactName(emergencyNameField.getText().trim());
            patient.setEmergencyContactPhone(emergencyPhoneField.getText().trim());
            patient.setBloodGroup((String) bloodField.getSelectedItem());
            patient.setAllergies(allergiesField.getText().trim());

            new PatientController().update(patient, user.getUsername());
            JOptionPane.showMessageDialog(this, "Patient record updated successfully.", "Update Patient", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        patientIdField.setText("");
        fullNameField.setText("");
        dobField.setText("");
        genderField.setSelectedIndex(0);
        contactField.setText("");
        addressField.setText("");
        emergencyNameField.setText("");
        emergencyPhoneField.setText("");
        bloodField.setSelectedIndex(0);
        allergiesField.setText("");
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
        JTextField field = new JTextField();
        field.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 230), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(new Color(250, 252, 255));
        return field;
    }

    private JTextArea styledArea() {
        JTextArea area = new JTextArea(3, 20);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 230), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        area.setBackground(new Color(250, 252, 255));
        return area;
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

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
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
