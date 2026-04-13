package com.mycompany.e.company.controller;

import com.mycompany.e.company.config.koneksi;
import com.mycompany.e.company.config.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    @FXML private ComboBox<String> cmbRole;

    @FXML
    public void initialize() {
        // Isi pilihan role saat halaman login dibuka
        cmbRole.getItems().addAll("Admin", "Employee");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String selectedRole = cmbRole.getValue(); // Ambil pilihan role

        if (username.isEmpty() || password.isEmpty() || selectedRole == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Lengkapi Username, Password, dan Role!");
            return;
        }

        try {
            Connection conn = koneksi.configDB();
            // Update query agar memeriksa username, password, DAN role
            String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, selectedRole.toLowerCase()); // misal: "Admin" jadi "admin"

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Simpan data ke Session
                UserSession.setSession(
                        rs.getInt("user_id"),
                        rs.getInt("company_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getInt("employee_id")
                );

                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Selamat Datang, " + username + "!");

                // Redirect berdasarkan role yang dipilih dan valid di DB
                String viewPath = selectedRole.equalsIgnoreCase("Admin") ? "/DashboardAdmin.fxml" : "/DashboardEmployee.fxml";

                Parent dashboard = FXMLLoader.load(getClass().getResource(viewPath));
                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

                // Ganti isinya saja agar transisi halus (tanpa merubah ukuran window)
                stage.getScene().setRoot(dashboard);

                // Jika ingin otomatis dimaximize setelah login, hilangkan tanda komen (//) di bawah ini:
                // stage.setMaximized(true);

            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Username, Password, atau Role Salah!");
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
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
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