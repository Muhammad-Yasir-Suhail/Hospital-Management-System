package com.hospital.ui.panels;

import com.hospital.controller.DoctorController;
import com.hospital.model.Doctor;
import com.hospital.model.User;
import com.hospital.utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class DoctorManagementPanel extends JPanel {
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Doctor ID", "Name", "Specialization", "Phone", "Days", "Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public DoctorManagementPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_BOTTOM);

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);

        loadDoctors("");
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 18, 8, 18));

        JLabel title = new JLabel("Doctor Management");
        title.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 22f));
        title.setForeground(UITheme.PRIMARY_DARK);

        JLabel subtitle = new JLabel("Search doctors, review availability, and deactivate records when required.");
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
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(0, 18, 18, 18));

        JPanel filterCard = new RoundedCard();
        filterCard.setLayout(new GridBagLayout());
        filterCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        JTextField queryField = styledField();
        queryField.setPreferredSize(new Dimension(280, 38));

        JButton searchButton = styledButton("Search", UITheme.PRIMARY);
        JButton refreshButton = styledButton("Refresh", UITheme.SECONDARY);
        JButton deactivateButton = styledButton("Deactivate", UITheme.ERROR);
        JButton addButton = styledButton("Add Doctor", UITheme.SUCCESS);

        searchButton.setPreferredSize(new Dimension(120, 40));
        refreshButton.setPreferredSize(new Dimension(120, 40));
        deactivateButton.setPreferredSize(new Dimension(140, 40));
        addButton.setPreferredSize(new Dimension(130, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        filterCard.add(sectionLabel("Search by Name / Specialization"), gbc);

        gbc.gridy = 1;
        filterCard.add(queryField, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(refreshButton);
        buttons.add(searchButton);
        buttons.add(addButton);
        buttons.add(deactivateButton);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0;
        filterCard.add(buttons, gbc);

        JTable table = createTable();
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.getVerticalScrollBar().setUnitIncrement(16);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());

        body.add(filterCard, BorderLayout.NORTH);
        body.add(tableScroll, BorderLayout.CENTER);

        searchButton.addActionListener(e -> loadDoctors(queryField.getText().trim()));
        queryField.addActionListener(e -> loadDoctors(queryField.getText().trim()));
        refreshButton.addActionListener(e -> {
            queryField.setText("");
            loadDoctors("");
        });
        addButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Use Add Doctor panel from sidebar.", "Add Doctor", JOptionPane.INFORMATION_MESSAGE));
        deactivateButton.addActionListener(e -> deactivateSelected(table));

        return body;
    }

    private JTable createTable() {
        JTable table = new JTable(tableModel);
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

        return table;
    }

    private void loadDoctors(String query) {
        try {
            tableModel.setRowCount(0);
            List<Doctor> doctors = new DoctorController().getAll(query);
            for (Doctor doctor : doctors) {
                tableModel.addRow(new Object[]{
                        doctor.getDoctorId(),
                        doctor.getFullName(),
                        doctor.getSpecialization(),
                        doctor.getContactNumber(),
                        doctor.getAvailableDays(),
                        doctor.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Doctor Management", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deactivateSelected(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a doctor row first.", "Doctor Management", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String doctorId = String.valueOf(tableModel.getValueAt(row, 0));
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deactivate doctor " + doctorId + "?",
                "Confirm Deactivation",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            new DoctorController().deactivate(doctorId);
            loadDoctors("");
            JOptionPane.showMessageDialog(this, "Doctor deactivated successfully.", "Doctor Management", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Doctor Management", JOptionPane.ERROR_MESSAGE);
        }
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
