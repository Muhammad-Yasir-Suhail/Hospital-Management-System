package com.hospital.ui.panels;

import com.hospital.controller.PatientController;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.utils.UITheme;

import javax.swing.*;
import java.awt.*;

public class PatientRegistrationPanel extends JPanel {
    public PatientRegistrationPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_BOTTOM);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

        JLabel title = new JLabel("Patient Registration");
        title.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 22f));
        title.setForeground(UITheme.PRIMARY_DARK);

        JLabel subtitle = new JLabel("Register a new patient using the form below");
        subtitle.setFont(UITheme.BODY_FONT);
        subtitle.setForeground(new Color(90, 90, 90));

        JPanel headerText = new JPanel();
        headerText.setOpaque(false);
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.add(title);
        headerText.add(Box.createVerticalStrut(4));
        headerText.add(subtitle);

        header.add(headerText, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel formCard = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(new Color(0, 0, 0, 24));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g2.dispose();
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        formCard.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        formCard.setOpaque(false);

        JTextField name = styledField();
        JTextField dob = styledField();
        JComboBox<String> gender = styledCombo(new String[]{"Male", "Female", "Other"});
        JTextField phone = styledField();
        JTextArea address = styledArea();
        JTextField emName = styledField();
        JTextField emPhone = styledField();
        JComboBox<String> blood = styledCombo(new String[]{"", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        JTextArea allergies = styledArea();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;
        row = addField(formCard, gbc, row, "Full Name*", name);
        row = addField(formCard, gbc, row, "DOB* (DD/MM/YYYY)", dob);
        row = addField(formCard, gbc, row, "Gender*", gender);
        row = addField(formCard, gbc, row, "Contact*", phone);
        row = addField(formCard, gbc, row, "Address", new JScrollPane(address));
        row = addField(formCard, gbc, row, "Emergency Contact Name", emName);
        row = addField(formCard, gbc, row, "Emergency Contact Phone", emPhone);
        row = addField(formCard, gbc, row, "Blood Group", blood);
        row = addField(formCard, gbc, row, "Known Allergies", new JScrollPane(allergies));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonRow.setOpaque(false);

        JButton save = styledButton("Save Patient", UITheme.PRIMARY);
        JButton clear = styledButton("Clear Form", UITheme.SECONDARY);
        save.setPreferredSize(new Dimension(150, 42));
        clear.setPreferredSize(new Dimension(150, 42));
        buttonRow.add(clear);
        buttonRow.add(save);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        formCard.add(buttonRow, gbc);

        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setOpaque(false);
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.gridx = 0;
        centerGbc.gridy = 0;
        centerGbc.weightx = 1.0;
        centerGbc.fill = GridBagConstraints.HORIZONTAL;
        centerGbc.insets = new Insets(0, 0, 0, 0);
        formContainer.add(formCard, centerGbc);

        JScrollPane scrollPane = new JScrollPane(formContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        JPanel scrollWrapper = new JPanel(new BorderLayout());
        scrollWrapper.setOpaque(false);
        scrollWrapper.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));
        scrollWrapper.add(scrollPane, BorderLayout.CENTER);

        add(scrollWrapper, BorderLayout.CENTER);

        save.addActionListener(e -> {
            try {
                Patient p = new Patient();
                PatientController pc = new PatientController();
                p.setFullName(name.getText()); p.setDateOfBirth(pc.parseDate(dob.getText())); p.setGender((String) gender.getSelectedItem()); p.setContactNumber(phone.getText()); p.setAddress(address.getText()); p.setEmergencyContactName(emName.getText()); p.setEmergencyContactPhone(emPhone.getText()); p.setBloodGroup((String) blood.getSelectedItem()); p.setAllergies(allergies.getText());
                String id = pc.register(p, user.getUsername());
                JOptionPane.showMessageDialog(this, "Patient saved. ID: " + id);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        });
        clear.addActionListener(e -> { name.setText(""); dob.setText(""); phone.setText(""); address.setText(""); emName.setText(""); emPhone.setText(""); allergies.setText(""); });
    }

    private JTextField styledField() {
        JTextField field = new JTextField();
        field.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 230), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(new Color(250, 252, 255));
        field.setPreferredSize(new Dimension(320, 38));
        return field;
    }

    private JComboBox<String> styledCombo(String[] values) {
        JComboBox<String> combo = new JComboBox<>(values);
        combo.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(new Dimension(320, 38));
        return combo;
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
        area.setPreferredSize(new Dimension(320, 78));
        return area;
    }

    private int addField(JPanel panel, GridBagConstraints gbc, int row, String label, Component field) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(UITheme.BODY_FONT.deriveFont(Font.BOLD, 13f));
        jLabel.setForeground(UITheme.PRIMARY_DARK);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(jLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);

        return row + 1;
    }

    private JButton styledButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(baseColor);
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
}
