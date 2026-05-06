package com.hospital.ui.panels;

import com.hospital.controller.BillingController;
import com.hospital.model.Bill;
import com.hospital.model.User;
import com.hospital.utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class BillingPanel extends JPanel {
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Bill ID", "Patient Name", "Total Amount", "Status", "Date"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public BillingPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_BOTTOM);

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 18, 8, 18));

        JLabel title = new JLabel("Billing");
        title.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 22f));
        title.setForeground(UITheme.PRIMARY_DARK);

        JLabel subtitle = new JLabel("Monitor paid and unpaid bills and navigate to generate or view details.");
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

        JComboBox<String> statusFilter = styledCombo(new String[]{"All", "Paid", "Unpaid"});
        JButton loadButton = styledButton("Load", UITheme.PRIMARY);
        JButton generateButton = styledButton("Generate Bill", UITheme.SUCCESS);
        JButton viewButton = styledButton("View Bill", UITheme.SECONDARY);

        loadButton.setPreferredSize(new Dimension(110, 40));
        generateButton.setPreferredSize(new Dimension(140, 40));
        viewButton.setPreferredSize(new Dimension(120, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        filterCard.add(sectionLabel("Payment Status"), gbc);

        gbc.gridy = 1;
        filterCard.add(statusFilter, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(loadButton);
        actions.add(generateButton);
        actions.add(viewButton);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0;
        filterCard.add(actions, gbc);

        JTable table = createTable();
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.getVerticalScrollBar().setUnitIncrement(16);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());

        body.add(filterCard, BorderLayout.NORTH);
        body.add(tableScroll, BorderLayout.CENTER);

        loadButton.addActionListener(e -> loadBills((String) statusFilter.getSelectedItem()));
        generateButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Use Generate Bill panel from sidebar.", "Billing", JOptionPane.INFORMATION_MESSAGE));
        viewButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Select a bill and open View Bill panel.", "Billing", JOptionPane.INFORMATION_MESSAGE));

        loadBills("All");
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

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object value, boolean selected, boolean focus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(tb, value, selected, focus, row, column);
                String status = String.valueOf(tb.getValueAt(row, 3));
                if (!selected) {
                    if ("Unpaid".equalsIgnoreCase(status)) {
                        comp.setForeground(UITheme.ERROR);
                    } else {
                        comp.setForeground(UITheme.SUCCESS);
                    }
                }
                return comp;
            }
        });

        return table;
    }

    private void loadBills(String status) {
        try {
            tableModel.setRowCount(0);
            List<Bill> bills = new BillingController().getBills(status == null ? "All" : status);
            for (Bill bill : bills) {
                tableModel.addRow(new Object[]{
                        bill.getBillId(),
                        bill.getPatientName(),
                        bill.getTotalAmount(),
                        bill.getPaymentStatus(),
                        bill.getBillDate()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Billing", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.BODY_FONT.deriveFont(Font.BOLD, 13f));
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
