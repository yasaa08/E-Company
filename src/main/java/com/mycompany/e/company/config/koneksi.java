package com.mycompany.e.company.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class koneksi {
    private static Connection mysqlkonek;

    public static Connection configDB() throws SQLException {
        try {
            // 1. MASUKKAN DATA DARI CLOUD MYSQL KAMU DI SINI:
            String host = "dbecompany-kennanelyasa-a84e.e.aivencloud.com"; // contoh: mysql-ecompany.aivencloud.com
            String port = "19843";         // contoh: 12345
            String dbName = "defaultdb";    // contoh: defaultdb
            String user = "avnadmin";         // contoh: avnadmin
            String pass = "AVNS_LoButVAWgEra2s5D49o";     // contoh: password123

            // 2. URL Otomatis Terbentuk (Tambahan sslMode=REQUIRED karena cloud biasanya wajib SSL)
            String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?sslMode=REQUIRED";

            // 3. Register & Buat Koneksi
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            mysqlkonek = DriverManager.getConnection(url, user, pass);

            System.out.println("Koneksi ke Cloud Database Berhasil!");

        } catch (SQLException e) {
            System.err.println("Koneksi Gagal: " + e.getMessage());
            throw new SQLException("Gagal Konek Database");
        }
        return mysqlkonek;
    }
}