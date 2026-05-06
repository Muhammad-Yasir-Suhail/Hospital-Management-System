package com.hospital.ui.panels;

import com.hospital.controller.AppointmentController;
import com.hospital.model.Appointment;
import com.hospital.model.User;
import com.hospital.utils.UITheme;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RescheduleAppointmentPanel extends JPanel {
    private final JTextField appointmentIdField = styledField();
    private final JTextField patientField = styledField(false);
    private final JTextField doctorField = styledField(false);
    private final JTextField previousDateField = styledField(false);
    private final JTextField previousTimeField = styledField(false);
    private final JDateChooser newDateChooser = new JDateChooser();
    private final JComboBox<String> newTimeField = new JComboBox<>();
    private final JLabel statusValue = styledValue();

    public RescheduleAppointmentPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_BOTTOM);

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(user), BorderLayout.CENTER);
        loadTimes();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 18, 8, 18));

        JLabel title = new JLabel("Reschedule Appointment");
        title.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 22f));
        title.setForeground(UITheme.PRIMARY_DARK);

        JLabel subtitle = new JLabel("Load an appointment by ID, review the existing details, and choose a new date and time.");
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

        JButton loadButton = styledButton("Load Details", UITheme.PRIMARY);
        JButton saveButton = styledButton("Save Changes", UITheme.SUCCESS);
        JButton clearButton = styledButton("Clear", UITheme.SECONDARY);
        loadButton.setPreferredSize(new Dimension(160, 40));
        saveButton.setPreferredSize(new Dimension(160, 42));
        clearButton.setPreferredSize(new Dimension(120, 40));

        newDateChooser.setDateFormatString("dd/MM/yyyy");
        newDateChooser.getJCalendar().setTodayButtonVisible(true);
        newDateChooser.getJCalendar().setWeekOfYearVisible(false);

        newTimeField.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        newTimeField.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;
        row = addField(card, gbc, row, "Appointment ID", appointmentIdField);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0;
        card.add(loadButton, gbc);

        row = addField(card, gbc, row, "Patient", patientField);
        row = addField(card, gbc, row, "Doctor", doctorField);
        row = addField(card, gbc, row, "Previous Date", previousDateField);
        row = addField(card, gbc, row, "Previous Time", previousTimeField);
        row = addField(card, gbc, row, "Status", statusValue);
        row = addField(card, gbc, row, "New Date", newDateChooser);
        row = addField(card, gbc, row, "New Time", newTimeField);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(clearButton);
        buttonRow.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        card.add(buttonRow, gbc);

        loadButton.addActionListener(e -> loadAppointment());
        saveButton.addActionListener(e -> saveChanges(user));
        clearButton.addActionListener(e -> clearForm());
        appointmentIdField.addActionListener(e -> loadAppointment());

        wrapper.add(card, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private void loadAppointment() {
        try {
            String id = appointmentIdField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter an appointment ID.", "Reschedule Appointment", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Appointment appointment = new AppointmentController().getById(id);
            if (appointment == null) {
                JOptionPane.showMessageDialog(this, "No appointment found for ID: " + id, "Reschedule Appointment", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                appointmentIdField.setText(id);
                return;
            }

            appointmentIdField.setText(appointment.getAppointmentId());
            patientField.setText(valueOrDash(appointment.getPatientName()));
            doctorField.setText(valueOrDash(appointment.getDoctorName()));
            previousDateField.setText(appointment.getAppointmentDate() != null ? appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-");
            previousTimeField.setText(appointment.getAppointmentTime() != null ? appointment.getAppointmentTime().toString() : "-");
            statusValue.setText(valueOrDash(appointment.getStatus()));
            newDateChooser.setDate(null);
            newTimeField.setSelectedItem(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveChanges(User user) {
        try {
            String id = appointmentIdField.getText().trim();
            if (id.isEmpty()) {
                throw new IllegalArgumentException("Enter an appointment ID first.");
            }
            if (newDateChooser.getDate() == null) {
                throw new IllegalArgumentException("Please select a new appointment date.");
            }
            if (newTimeField.getSelectedItem() == null) {
                throw new IllegalArgumentException("Please select a new appointment time.");
            }
            java.sql.Date newDate = new java.sql.Date(newDateChooser.getDate().getTime());
            java.sql.Time time = java.sql.Time.valueOf((String) newTimeField.getSelectedItem() + ":00");
            new AppointmentController().reschedule(id, newDate.toLocalDate(), time, user.getUsername());
            JOptionPane.showMessageDialog(this, "Appointment rescheduled successfully.", "Reschedule Appointment", JOptionPane.INFORMATION_MESSAGE);
            loadAppointment();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Reschedule Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        appointmentIdField.setText("");
        patientField.setText("");
        doctorField.setText("");
        previousDateField.setText("");
        previousTimeField.setText("");
        statusValue.setText("-");
        newDateChooser.setDate(null);
        if (newTimeField.getItemCount() > 0) {
            newTimeField.setSelectedIndex(0);
        }
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
        return styledField(true);
    }

    private JTextField styledField(boolean editable) {
        JTextField field = new JTextField();
        field.setEditable(editable);
        field.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 230), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(editable ? new Color(250, 252, 255) : new Color(244, 247, 250));
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

    private void loadTimes() {
        newTimeField.removeAllItems();
        for (int hour = 9; hour <= 17; hour++) {
            newTimeField.addItem(String.format("%02d:00", hour));
            newTimeField.addItem(String.format("%02d:30", hour));
        }
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
        RoundedCard() { setOpaque(false); }

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
