package com.hospital.ui;

import com.hospital.controller.AuthController;
import com.hospital.model.User;
import com.hospital.utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginScreen extends JFrame {
    public LoginScreen() {
        setTitle("Hospital Management System - Login");
        setSize(560, 430);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel loginCard = new RoundedCard();
        loginCard.setLayout(new GridBagLayout());
        loginCard.setBorder(new EmptyBorder(26, 26, 26, 26));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel header = new JLabel("Hospital Management System");
        header.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 24f));
        header.setForeground(UITheme.PRIMARY_DARK);

        JLabel sub = new JLabel("Please sign in");
        sub.setFont(UITheme.BODY_FONT);
        sub.setForeground(new Color(90, 90, 90));

        JTextField username = styledField();
        JPasswordField password = styledPasswordField();

        Dimension fieldSize = new Dimension(360, 40);
        username.setPreferredSize(fieldSize);
        password.setPreferredSize(fieldSize);

        JButton login = new RoundedButton("Login");
        login.setPreferredSize(new Dimension(360, 44));
        login.setFont(UITheme.BUTTON_FONT);
        login.setBackground(UITheme.PRIMARY);
        login.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginCard.add(header, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 8, 12, 8);
        loginCard.add(sub, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(8, 8, 4, 8);
        loginCard.add(sectionLabel("Username"), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 8, 10, 8);
        gbc.weightx = 1.0;
        loginCard.add(username, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(8, 8, 4, 8);
        loginCard.add(sectionLabel("Password"), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 8, 12, 8);
        loginCard.add(password, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(10, 8, 6, 8);
        gbc.weightx = 0;
        loginCard.add(login, gbc);

        login.addActionListener(e -> {
            try {
                User user = new AuthController().login(username.getText(), new String(password.getPassword()));
                if (user == null) {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                new MainDashboard(user).setVisible(true);
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(loginCard, BorderLayout.CENTER);
        add(panel);
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

    private JPasswordField styledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 230), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(new Color(250, 252, 255));
        return field;
    }

    // Gradient background panel for a modern look
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, UITheme.BG_TOP, 0, h, UITheme.BG_BOTTOM);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
        }
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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            g2.setColor(new Color(0, 0, 0, 24));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Rounded button for consistent modern appearance
    private static class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            setFocusPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), UITheme.BUTTON_RADIUS, UITheme.BUTTON_RADIUS);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        public void setOpaque(boolean isOpaque) {
            super.setOpaque(false);
        }
    }
}
