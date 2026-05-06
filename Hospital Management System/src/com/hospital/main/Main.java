package com.hospital.main;

import com.hospital.ui.LoginScreen;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
