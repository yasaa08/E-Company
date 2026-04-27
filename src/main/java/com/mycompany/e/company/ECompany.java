package com.mycompany.e.company;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class ECompany extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/LoginView.fxml"));

        // Ubah ke ukuran standar Laptop (HD) biar nggak terlalu melar
        Scene scene = new Scene(root, 1280, 720);

        stage.setTitle("E-Company System");
        stage.setScene(scene);

        // Kunci ukuran layar
        stage.setResizable(false);

        // Menengahkan layar
        stage.centerOnScreen();

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
