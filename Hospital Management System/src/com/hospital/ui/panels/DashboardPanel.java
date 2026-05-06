package com.hospital.ui.panels;

import com.hospital.controller.AppointmentController;
import com.hospital.controller.BillingController;
import com.hospital.controller.DoctorController;
import com.hospital.controller.PatientController;
import com.hospital.model.User;
import com.hospital.utils.UITheme;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private final JLabel p = new JLabel();
    private final JLabel a = new JLabel();
    private final JLabel d = new JLabel();
    private final JLabel b = new JLabel();

    public DashboardPanel(User user) {
        setLayout(new BorderLayout(12,12));
        setBackground(UITheme.BG_BOTTOM);

        JLabel welcome = new JLabel("Welcome, " + user.getFullName() + " — " + user.getRole());
        welcome.setFont(UITheme.HEADER_FONT);
        welcome.setBorder(BorderFactory.createEmptyBorder(12,12,0,12));
        add(welcome, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 4, 16, 16));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));

        center.add(infoCard("Total Patients", p, UITheme.PRIMARY));
        center.add(infoCard("Today's Appointments", a, UITheme.SECONDARY));
        center.add(infoCard("Active Doctors", d, UITheme.PRIMARY_DARK));
        center.add(infoCard("Unpaid Bills", b, UITheme.ERROR));

        add(center, BorderLayout.CENTER);

        JButton refresh = styledButton("Refresh");
        refresh.addActionListener(e -> refresh());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setOpaque(false);
        south.add(refresh);
        add(south, BorderLayout.SOUTH);

        refresh();
    }

    private JPanel infoCard(String title, JLabel valueLabel, Color accent) {
        Card card = new Card(accent);
        card.setPreferredSize(new Dimension(220, 150));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.BODY_FONT.deriveFont(Font.BOLD, 16f));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 36f));
        valueLabel.setForeground(UITheme.PRIMARY_DARK);

        JPanel topContent = new JPanel(new BorderLayout());
        topContent.setOpaque(false);
        topContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topContent.add(titleLabel, BorderLayout.CENTER);

        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inner.add(topContent, BorderLayout.NORTH);
        inner.add(valueLabel, BorderLayout.CENTER);

        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    private JButton styledButton(String text) {
        JButton b = new JButton(text);
        b.setFont(UITheme.BUTTON_FONT);
        b.setBackground(UITheme.PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    // Small rounded card panel with accent header
    private static class Card extends JPanel {
        private final Color accent;
        public Card(Color accent) {
            this.accent = accent;
            setLayout(new BorderLayout());
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // background
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w, h, 20, 20);

            // top accent bar
            g2.setColor(accent);
            g2.fillRoundRect(0, 0, w, 56, 20, 20);

            // subtle border
            g2.setColor(new Color(0,0,0,20));
            g2.drawRoundRect(0, 0, w-1, h-1, 20, 20);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public boolean isOpaque() { return false; }
    }

    private void refresh() {
        try {
            p.setText(String.valueOf(new PatientController().totalPatients()));
            a.setText(String.valueOf(new AppointmentController().todaysAppointments()));
            d.setText(String.valueOf(new DoctorController().totalActive()));
            b.setText(String.valueOf(new BillingController().unpaidCount()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
