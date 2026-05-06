package com.hospital.ui.panels;

import com.hospital.controller.DoctorController;
import com.hospital.model.Doctor;
import com.hospital.model.User;
import com.hospital.utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AddDoctorPanel extends JPanel {
    private final JTextField nameField = styledField();
    private final JTextField specializationField = styledField();
    private final JTextField phoneField = styledField();
    private final JTextField emailField = styledField();
    private final JCheckBox[] dayChecks = {
            styledCheckBox("Mon"), styledCheckBox("Tue"), styledCheckBox("Wed"),
            styledCheckBox("Thu"), styledCheckBox("Fri"), styledCheckBox("Sat"), styledCheckBox("Sun")
    };
    private final JComboBox<String> startCombo = styledCombo(new String[]{"08:00", "09:00", "10:00"});
    private final JComboBox<String> endCombo = styledCombo(new String[]{"14:00", "16:00", "17:00"});
    private final JComboBox<String> statusCombo = styledCombo(new String[]{"Active", "Inactive"});

    public AddDoctorPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_BOTTOM);

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(user), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 18, 8, 18));

        JLabel title = new JLabel("Add Doctor");
        title.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 22f));
        title.setForeground(UITheme.PRIMARY_DARK);

        JLabel subtitle = new JLabel("Create a doctor profile with availability and shift timing details.");
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

        JPanel dayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        dayPanel.setOpaque(false);
        for (JCheckBox dayCheck : dayChecks) {
            dayPanel.add(dayCheck);
        }

        JButton saveButton = styledButton("Save Doctor", UITheme.SUCCESS);
        JButton clearButton = styledButton("Clear", UITheme.SECONDARY);
        saveButton.setPreferredSize(new Dimension(150, 42));
        clearButton.setPreferredSize(new Dimension(120, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;
        row = addField(card, gbc, row, "Full Name*", nameField);
        row = addField(card, gbc, row, "Specialization*", specializationField);
        row = addField(card, gbc, row, "Contact Number", phoneField);
        row = addField(card, gbc, row, "Email", emailField);
        row = addField(card, gbc, row, "Available Days", dayPanel);
        row = addField(card, gbc, row, "Shift Start", startCombo);
        row = addField(card, gbc, row, "Shift End", endCombo);
        row = addField(card, gbc, row, "Status", statusCombo);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(clearButton);
        buttonRow.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        card.add(buttonRow, gbc);

        saveButton.addActionListener(e -> saveDoctor(user));
        clearButton.addActionListener(e -> clearForm());

        wrapper.add(card, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private void saveDoctor(User user) {
        try {
            String name = nameField.getText().trim();
            String specialization = specializationField.getText().trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Full name is required.");
            }
            if (specialization.isEmpty()) {
                throw new IllegalArgumentException("Specialization is required.");
            }

            Doctor doctor = new Doctor();
            doctor.setFullName(name);
            doctor.setSpecialization(specialization);
            doctor.setContactNumber(phoneField.getText().trim());
            doctor.setEmail(emailField.getText().trim());
            doctor.setAvailableDays(Arrays.stream(dayChecks)
                    .filter(AbstractButton::isSelected)
                    .map(AbstractButton::getText)
                    .collect(Collectors.joining(",")));
            doctor.setShiftStart(LocalTime.parse(startCombo.getSelectedItem() + ":00"));
            doctor.setShiftEnd(LocalTime.parse(endCombo.getSelectedItem() + ":00"));
            doctor.setStatus((String) statusCombo.getSelectedItem());

            new DoctorController().save(doctor, false);
            JOptionPane.showMessageDialog(this, "Doctor added successfully.", "Add Doctor", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Add Doctor", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        nameField.setText("");
        specializationField.setText("");
        phoneField.setText("");
        emailField.setText("");
        for (JCheckBox dayCheck : dayChecks) {
            dayCheck.setSelected(false);
        }
        startCombo.setSelectedIndex(0);
        endCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
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

    private JCheckBox styledCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(UITheme.BODY_FONT.deriveFont(Font.BOLD, 12f));
        checkBox.setOpaque(false);
        checkBox.setForeground(UITheme.PRIMARY_DARK);
        return checkBox;
    }

    private JComboBox<String> styledCombo(String[] options) {
        JComboBox<String> combo = new JComboBox<>(options);
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
