package com.hospital.ui;

import com.hospital.model.User;
import com.hospital.ui.panels.*;
import com.hospital.utils.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainDashboard extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private String activePage;

    public MainDashboard(User user) {
        setTitle("Hospital Management System");
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UITheme.SECONDARY);
        top.add(new JLabel("  Logged in: " + user.getFullName() + " (" + user.getRole() + ")"), BorderLayout.WEST);
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> { new LoginScreen().setVisible(true); dispose(); });
        top.add(logout, BorderLayout.EAST);

        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.PRIMARY);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        Map<String, JPanel> panels = new LinkedHashMap<>();
        panels.put("Dashboard", new DashboardPanel(user));
        panels.put("Patient Registration", new PatientRegistrationPanel(user));
        panels.put("Patient Search", new PatientSearchPanel(user));
        panels.put("Patient Profile", new PatientProfilePanel(user));
        panels.put("Update Patient", new UpdatePatientPanel(user));
        panels.put("Book Appointment", new BookAppointmentPanel(user));
        panels.put("Appointment Schedule", new AppointmentSchedulePanel(user));
        panels.put("Reschedule Appointment", new RescheduleAppointmentPanel(user));
        panels.put("Doctor Management", new DoctorManagementPanel(user));
        panels.put("Add Doctor", new AddDoctorPanel(user));
        panels.put("Billing", new BillingPanel(user));
        panels.put("Generate Bill", new GenerateBillPanel(user));
        panels.put("View Bill", new ViewBillPanel(user));
        panels.put("Reports", new AdminReportPanel(user));

        // uniform button size and style
        Dimension sidebarBtnSize = new Dimension(200, 44);
        for (Map.Entry<String, JPanel> entry : panels.entrySet()) {
            String role = user.getRole();
            if (role.equals("Doctor") && !(entry.getKey().equals("Dashboard") || entry.getKey().equals("Appointment Schedule") || entry.getKey().equals("View Bill"))) continue;
            if (role.equals("Receptionist") && entry.getKey().equals("Doctor Management")) continue;
            if (!role.equals("Admin") && entry.getKey().equals("Reports")) continue;
            JButton btn = new JButton(entry.getKey());
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setPreferredSize(sidebarBtnSize);
            btn.setMaximumSize(sidebarBtnSize);
            btn.setMinimumSize(sidebarBtnSize);
            btn.setFont(UITheme.BUTTON_FONT);
            styleSidebarButton(btn, false);
            btn.setFocusPainted(false);
            btn.addActionListener(e -> showPanel(entry.getKey()));
            navButtons.put(entry.getKey(), btn);
            sidebar.add(Box.createVerticalStrut(8));
            sidebar.add(btn);
            content.add(entry.getValue(), entry.getKey());
        }

        JScrollPane sidebarScroll = new JScrollPane(sidebar);
        sidebarScroll.setPreferredSize(new Dimension(240, 0));
        sidebarScroll.setBorder(null);
        sidebarScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScroll.getVerticalScrollBar().setUnitIncrement(16);
        sidebarScroll.getViewport().setBackground(UITheme.PRIMARY);
        sidebarScroll.setBackground(UITheme.PRIMARY);

        add(top, BorderLayout.NORTH);
        add(sidebarScroll, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        if (!navButtons.isEmpty()) {
            showPanel(navButtons.keySet().iterator().next());
        } else {
            cardLayout.first(content);
        }
    }

    private void showPanel(String name) {
        cardLayout.show(content, name);
        if (activePage != null && navButtons.containsKey(activePage)) {
            styleSidebarButton(navButtons.get(activePage), false);
        }
        activePage = name;
        if (navButtons.containsKey(name)) {
            styleSidebarButton(navButtons.get(name), true);
        }
    }

    private void styleSidebarButton(JButton button, boolean active) {
        if (active) {
            button.setBackground(UITheme.ACCENT);
            button.setForeground(UITheme.PRIMARY_DARK);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 1, true),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)
            ));
        } else {
            button.setBackground(UITheme.SECONDARY);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)
            ));
        }
        button.setOpaque(true);
        button.setContentAreaFilled(true);
    }
}
