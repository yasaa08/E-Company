package com.mycompany.e.company; 

import com.formdev.flatlaf.FlatLightLaf; 
import javax.swing.UIManager;
import com.mycompany.e.company.view.LoginForm; 

public class ECompany {

    public static void main(String[] args) {
        // 1. PASANG TEMA FLATLAF DI SINI
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 15);
            System.out.println("Tema FlatLaf Berhasil Dipasang!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. BUKA HALAMAN LOGIN
        java.awt.EventQueue.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}