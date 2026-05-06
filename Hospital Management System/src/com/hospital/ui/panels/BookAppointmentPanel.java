package com.hospital.ui.panels;

import com.hospital.controller.AppointmentController;
import com.hospital.controller.DoctorController;
import com.hospital.controller.PatientController;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.utils.UITheme;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class BookAppointmentPanel extends JPanel {
    private final JTextField patientIdField = styledField();
    private final JTextField patientNameField = styledField(false);
    private final JComboBox<Doctor> doctorCombo = new JComboBox<>();
    private final JDateChooser dateChooser = new JDateChooser();
    private final JComboBox<String> timeCombo = new JComboBox<>();
    private final JTextArea reasonArea = styledArea();

    public BookAppointmentPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_BOTTOM);

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(user), BorderLayout.CENTER);
        loadDoctors();
        loadTimes();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 18, 8, 18));

        JLabel title = new JLabel("Book Appointment");
        title.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 22f));
        title.setForeground(UITheme.PRIMARY_DARK);

        JLabel subtitle = new JLabel("Create a new appointment using a patient ID, doctor, date, and time slot.");
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

        doctorCombo.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        doctorCombo.setBackground(Color.WHITE);
        doctorCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Doctor doctor) {
                    setText(doctor.getDoctorId() + " - " + doctor.getFullName());
                }
                return this;
            }
        });

        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.getJCalendar().setTodayButtonVisible(true);
        dateChooser.getJCalendar().setWeekOfYearVisible(false);
        timeCombo.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        reasonArea.setRows(4);

        JButton loadPatient = styledButton("Load Patient", UITheme.SECONDARY);
        JButton bookButton = styledButton("Book Appointment", UITheme.PRIMARY);
        JButton clearButton = styledButton("Clear", UITheme.SECONDARY);
        loadPatient.setPreferredSize(new Dimension(140, 40));
        bookButton.setPreferredSize(new Dimension(170, 42));
        clearButton.setPreferredSize(new Dimension(120, 40));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(clearButton);
        buttonRow.add(bookButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JPanel patientLookup = new JPanel(new BorderLayout(10, 0));
        patientLookup.setOpaque(false);
        patientLookup.add(patientIdField, BorderLayout.CENTER);
        patientLookup.add(loadPatient, BorderLayout.EAST);

        int row = 0;
        row = addField(card, gbc, row, "Patient ID", patientLookup);

        row = addField(card, gbc, row, "Patient Name", patientNameField);
        row = addField(card, gbc, row, "Doctor", doctorCombo);
        row = addField(card, gbc, row, "Appointment Date", dateChooser);
        row = addField(card, gbc, row, "Appointment Time", timeCombo);
        row = addField(card, gbc, row, "Reason", new JScrollPane(reasonArea));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        card.add(buttonRow, gbc);

        loadPatient.addActionListener(e -> loadPatient());
        bookButton.addActionListener(e -> bookAppointment(user));
        clearButton.addActionListener(e -> clearForm());
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
            String patientId = patientIdField.getText().trim();
            if (patientId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a patient ID first.", "Book Appointment", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Patient patient = new PatientController().getById(patientId);
            if (patient == null) {
                JOptionPane.showMessageDialog(this, "No patient found for ID: " + patientId, "Book Appointment", JOptionPane.INFORMATION_MESSAGE);
                patientNameField.setText("");
                return;
            }
            patientNameField.setText(patient.getFullName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Book Appointment", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bookAppointment(User user) {
        try {
            Appointment appointment = new Appointment();
            String patientId = patientIdField.getText().trim();
            if (patientId.isEmpty()) {
                throw new IllegalArgumentException("Patient ID is required.");
            }
            if (patientNameField.getText().trim().isEmpty()) {
                loadPatient();
            }
            Doctor doctor = (Doctor) doctorCombo.getSelectedItem();
            if (doctor == null) {
                throw new IllegalArgumentException("Please select a doctor.");
            }

            if (dateChooser.getDate() == null) {
                throw new IllegalArgumentException("Please select an appointment date.");
            }
            appointment.setPatientId(patientId);
            appointment.setDoctorId(doctor.getDoctorId());
            appointment.setAppointmentDate(new java.sql.Date(dateChooser.getDate().getTime()).toLocalDate());
            appointment.setAppointmentTime(LocalTime.parse((String) timeCombo.getSelectedItem()));
            appointment.setReason(reasonArea.getText().trim());

            String id = new AppointmentController().book(appointment, user.getUsername());
            JOptionPane.showMessageDialog(this, "Appointment booked successfully. ID: " + id, "Book Appointment", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Book Appointment", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        patientIdField.setText("");
        patientNameField.setText("");
        dateChooser.setDate(null);
        reasonArea.setText("");
        if (doctorCombo.getItemCount() > 0) {
            doctorCombo.setSelectedIndex(0);
        }
        if (timeCombo.getItemCount() > 0) {
            timeCombo.setSelectedIndex(0);
        }
    }

    private void loadDoctors() {
        try {
            doctorCombo.removeAllItems();
            List<Doctor> doctors = new DoctorController().getActive();
            for (Doctor doctor : doctors) {
                doctorCombo.addItem(doctor);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Doctors", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTimes() {
        timeCombo.removeAllItems();
        for (LocalTime time = LocalTime.of(9, 0); !time.isAfter(LocalTime.of(17, 0)); time = time.plusMinutes(30)) {
            timeCombo.addItem(time.toString());
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
        if (row == 0) {
            gbc.gridwidth = 1;
        }
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

    private JTextArea styledArea() {
        JTextArea area = new JTextArea();
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
