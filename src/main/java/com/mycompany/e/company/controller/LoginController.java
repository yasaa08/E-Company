package com.mycompany.e.company.controller;

import com.mycompany.e.company.config.koneksi;
import com.mycompany.e.company.config.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Username dan Password tidak boleh kosong!");
            return;
        }

        try {
            Connection conn = koneksi.configDB();
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Simpan data ke Session (sama kayak logic lama kamu)
                UserSession.setSession(
                        rs.getInt("user_id"),
                        rs.getInt("company_id"),
                        rs.getString("username"),
                        rs.getString("role")
                );

                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Selamat Datang, " + username + "!");

                // Cek Role & Pindah Halaman
                if ("admin".equals(rs.getString("role"))) {
                    Parent dashboard = FXMLLoader.load(getClass().getResource("/DashboardAdmin.fxml"));
                    Scene scene = new Scene(dashboard);

                    // Ambil stage sekarang
                    Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

                    // Set scene baru
                    stage.setScene(scene);

                    // Paksa Maximized
                    stage.setMaximized(true);
                    stage.setFullScreen(false); // Jangan Fullscreen mode game, tapi Maximized window biasa
                    stage.show();
                } else {
                    Parent dashboard = FXMLLoader.load(getClass().getResource("/DashboardEmployee.fxml"));
                    // ... logic sama seperti di atas
                }

                // Tutup jendela login
                //((Stage)((Node)event.getSource()).getScene().getWindow()).close();

            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Username atau Password Salah!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error: " + e.getMessage());
        }
    }

    @FXML
    private void bukaRegister(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/RegisterView.fxml"));
            // Ambil stage yang sedang aktif
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            // Ganti isinya saja, jadi window gak kedip/berubah ukuran
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}