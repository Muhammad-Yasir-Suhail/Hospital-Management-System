package com.hospital.ui.panels;

import com.hospital.controller.AppointmentController;
import com.hospital.controller.DoctorController;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.User;
import com.hospital.utils.UITheme;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppointmentSchedulePanel extends JPanel {
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Appt ID", "Patient Name", "Doctor", "Date", "Time", "Reason", "Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public AppointmentSchedulePanel(User user) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_BOTTOM);

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(user), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 18, 8, 18));

        JLabel title = new JLabel("Appointment Schedule");
        title.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 22f));
        title.setForeground(UITheme.PRIMARY_DARK);

        JLabel subtitle = new JLabel("Filter appointments by date or doctor and manage their status.");
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
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 18, 18, 18));

        JPanel filterCard = new RoundedCard();
        filterCard.setLayout(new GridBagLayout());
        filterCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.getJCalendar().setTodayButtonVisible(true);
        dateChooser.getJCalendar().setWeekOfYearVisible(false);

        JComboBox<Doctor> doctorCombo = new JComboBox<>();
        doctorCombo.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        doctorCombo.setBackground(Color.WHITE);
        doctorCombo.addItem(null);
        try {
            for (Doctor doctor : new DoctorController().getAll("")) {
                doctorCombo.addItem(doctor);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Doctors", JOptionPane.ERROR_MESSAGE);
        }
        doctorCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Doctor doctor) {
                    setText(doctor.getFullName() + " (" + doctor.getDoctorId() + ")");
                } else {
                    setText("All Doctors");
                }
                return this;
            }
        });

        JButton searchButton = styledButton("Search", UITheme.PRIMARY);
        JButton refreshButton = styledButton("Refresh", UITheme.SECONDARY);
        JButton completeButton = styledButton("Mark Complete", UITheme.SUCCESS);
        JButton cancelButton = styledButton("Cancel", UITheme.ERROR);
        JButton rescheduleButton = styledButton("Reschedule", UITheme.PRIMARY_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        filterCard.add(sectionLabel("Date"), gbc);
        gbc.gridy = 1;
        filterCard.add(dateChooser, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        filterCard.add(sectionLabel("Doctor"), gbc);
        gbc.gridy = 1;
        filterCard.add(doctorCombo, gbc);

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);
        actionButtons.add(refreshButton);
        actionButtons.add(searchButton);
        actionButtons.add(rescheduleButton);
        actionButtons.add(cancelButton);
        actionButtons.add(completeButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        filterCard.add(actionButtons, gbc);

        JTable table = createTable();
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.getVerticalScrollBar().setUnitIncrement(16);

        wrapper.add(filterCard, BorderLayout.NORTH);
        wrapper.add(tableScroll, BorderLayout.CENTER);

        searchButton.addActionListener(e -> loadAppointments(dateChooser, doctorCombo));
        refreshButton.addActionListener(e -> {
            dateChooser.setDate(null);
            doctorCombo.setSelectedIndex(0);
            loadAppointments(dateChooser, doctorCombo);
        });
        completeButton.addActionListener(e -> changeSelected(table, id -> new AppointmentController().complete(id, user.getUsername())));
        cancelButton.addActionListener(e -> changeSelected(table, id -> {
            String reason = JOptionPane.showInputDialog(this, "Reason for cancellation");
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("Cancellation reason is required.");
            }
            new AppointmentController().cancel(id, reason.trim(), user.getUsername());
        }));
        rescheduleButton.addActionListener(e -> openRescheduleDialog(table, user));

        loadAppointments(dateChooser, doctorCombo);
        return wrapper;
    }

    private JTable createTable() {
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 13f));
        table.setSelectionBackground(new Color(220, 236, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(UITheme.BUTTON_FONT);
        header.setBackground(UITheme.PRIMARY);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object value, boolean selected, boolean focus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(tb, value, selected, focus, row, column);
                String status = String.valueOf(tb.getValueAt(row, 6));
                if (!selected) {
                    if ("Completed".equalsIgnoreCase(status)) {
                        comp.setForeground(UITheme.SUCCESS);
                    } else if ("Cancelled".equalsIgnoreCase(status)) {
                        comp.setForeground(UITheme.ERROR);
                    } else {
                        comp.setForeground(UITheme.PRIMARY_DARK);
                    }
                }
                return comp;
            }
        });

        return table;
    }

    private void loadAppointments(JDateChooser dateChooser, JComboBox<Doctor> doctorCombo) {
        try {
            model.setRowCount(0);
            LocalDate date = dateChooser.getDate() == null
                    ? null
                    : new java.sql.Date(dateChooser.getDate().getTime()).toLocalDate();
            Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();
            String doctorId = selectedDoctor != null ? selectedDoctor.getDoctorId() : "";
            List<Appointment> appointments = new AppointmentController().schedule(date, doctorId);
            
            for (Appointment appointment : appointments) {
                model.addRow(new Object[]{
                        appointment.getAppointmentId(),
                        appointment.getPatientName(),
                        appointment.getDoctorName(),
                        appointment.getAppointmentDate(),
                        appointment.getAppointmentTime(),
                        appointment.getReason(),
                        appointment.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Schedule Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeSelected(JTable table, RowOperation op) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an appointment row first.", "Schedule", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            op.run(String.valueOf(model.getValueAt(row, 0)));
            JOptionPane.showMessageDialog(this, "Action completed successfully.", "Schedule", JOptionPane.INFORMATION_MESSAGE);
            table.clearSelection();
            loadAllAppointments();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Schedule Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRescheduleDialog(JTable table, User user) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an appointment row to reschedule.", "Schedule", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String appointmentId = String.valueOf(model.getValueAt(row, 0));
        String currentDate = String.valueOf(model.getValueAt(row, 3));
        String currentTime = String.valueOf(model.getValueAt(row, 4));

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.getJCalendar().setTodayButtonVisible(true);
        try {
            dateChooser.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(currentDate));
        } catch (Exception ignore) {
        }
        
        JComboBox<String> timeCombo = new JComboBox<>();
        for (int hour = 9; hour <= 17; hour++) {
            timeCombo.addItem(String.format("%02d:00", hour));
            timeCombo.addItem(String.format("%02d:30", hour));
        }
        timeCombo.setSelectedItem(currentTime);

        int option = JOptionPane.showConfirmDialog(
                this,
                new Object[]{"New Date", dateChooser, "New Time", timeCombo},
                "Reschedule Appointment",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (option == JOptionPane.OK_OPTION) {
            try {
                if (dateChooser.getDate() == null) {
                    throw new IllegalArgumentException("Please select a new appointment date.");
                }
                new AppointmentController().reschedule(
                        appointmentId,
                        new java.sql.Date(dateChooser.getDate().getTime()).toLocalDate(),
                        java.sql.Time.valueOf(((String) timeCombo.getSelectedItem()) + ":00"),
                        user.getUsername()
                );
                JOptionPane.showMessageDialog(this, "Appointment rescheduled successfully.", "Schedule", JOptionPane.INFORMATION_MESSAGE);
                loadAllAppointments();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Reschedule Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadAllAppointments() {
        JDateChooser emptyDateChooser = new JDateChooser();
        JComboBox<Doctor> emptyDoctorCombo = new JComboBox<>();
        emptyDoctorCombo.addItem(null);
        loadAppointments(emptyDateChooser, emptyDoctorCombo);
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

    private JComboBox<String> styledCombo() {
        JComboBox<String> combo = new JComboBox<>();
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

    private interface RowOperation {
        void run(String id) throws Exception;
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
