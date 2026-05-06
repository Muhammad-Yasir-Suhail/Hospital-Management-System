package com.hospital.ui.panels;

import com.hospital.controller.PatientController;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class PatientSearchPanel extends JPanel {
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Patient ID", "Name", "DOB", "Gender", "Contact"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public PatientSearchPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_BOTTOM);

        JPanel header = createHeader();
        add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(0, 18, 18, 18));

        JPanel searchCard = createSearchCard();
        body.add(searchCard, BorderLayout.NORTH);

        JTable table = createTable();
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.getVerticalScrollBar().setUnitIncrement(16);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        body.add(tableScroll, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 18, 8, 18));

        JLabel title = new JLabel("Patient Search");
        title.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 22f));
        title.setForeground(UITheme.PRIMARY_DARK);

        JLabel subtitle = new JLabel("Search patients by ID, name, or phone and review records instantly.");
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

    private JPanel createSearchCard() {
        JPanel card = new RoundedCard();
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JRadioButton byId = styledRadio("By ID", true);
        JRadioButton byName = styledRadio("By Name", false);
        JRadioButton byPhone = styledRadio("By Phone", false);
        ButtonGroup group = new ButtonGroup();
        group.add(byId);
        group.add(byName);
        group.add(byPhone);

        JTextField query = styledField();
        query.setPreferredSize(new Dimension(260, 38));

        JButton search = styledButton("Search", UITheme.PRIMARY);
        search.setPreferredSize(new Dimension(140, 40));

        JButton clear = styledButton("Clear", UITheme.SECONDARY);
        clear.setPreferredSize(new Dimension(140, 40));

        JPanel radioRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radioRow.setOpaque(false);
        radioRow.add(byId);
        radioRow.add(byName);
        radioRow.add(byPhone);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(radioRow, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        card.add(query, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(clear);
        buttons.add(search);
        card.add(buttons, gbc);

        search.addActionListener(e -> loadResults(byId, byName, byPhone, query));
        query.addActionListener(e -> loadResults(byId, byName, byPhone, query));
        clear.addActionListener(e -> {
            query.setText("");
            tableModel.setRowCount(0);
        });

        return card;
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

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        JOptionPane.showMessageDialog(
                                PatientSearchPanel.this,
                                "Selected patient: " + tableModel.getValueAt(row, 0),
                                "Patient Selected",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
            }
        });

        return table;
    }

    private void loadResults(JRadioButton byId, JRadioButton byName, JRadioButton byPhone, JTextField query) {
        try {
            tableModel.setRowCount(0);
            String mode = byId.isSelected() ? "By ID" : byPhone.isSelected() ? "By Phone" : "By Name";
            List<Patient> list = new PatientController().search(mode, query.getText().trim());
            for (Patient patient : list) {
                tableModel.addRow(new Object[]{
                        patient.getPatientId(),
                        patient.getFullName(),
                        patient.getDateOfBirth(),
                        patient.getGender(),
                        patient.getContactNumber()
                });
            }
            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No records found.", "Search", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Search Error", JOptionPane.ERROR_MESSAGE);
        }
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

    private JRadioButton styledRadio(String text, boolean selected) {
        JRadioButton radio = new JRadioButton(text, selected);
        radio.setFont(UITheme.BODY_FONT.deriveFont(Font.BOLD, 13f));
        radio.setForeground(UITheme.PRIMARY_DARK);
        radio.setOpaque(false);
        return radio;
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
