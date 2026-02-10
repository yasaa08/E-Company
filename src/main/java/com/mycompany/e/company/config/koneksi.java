/*
 * Pastikan baris ini sesuai dengan nama paket tempat kamu menyimpan file ini.
 * Kalau merah, biasanya NetBeans akan kasih lampu kuning di kiri untuk "Fix Imports" atau "Move Class".
 */
package com.mycompany.e.company.config; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class koneksi {
    
    private static Connection mysqlkonek;
    
    public static Connection configDB() throws SQLException {
        try {
            // 1. URL Database
            String url = "jdbc:mysql://localhost:3306/db_ecompany"; 
            
            // 2. User & Pass default XAMPP
            String user = "root"; 
            String pass = ""; 
            
            // 3. Register Driver MySQL
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            
            // 4. Buat koneksi
            mysqlkonek = DriverManager.getConnection(url, user, pass);
            
            System.out.println("Koneksi ke Database Berhasil!");
            
        } catch (SQLException e) {
            System.err.println("Koneksi Gagal: " + e.getMessage()); 
            throw new SQLException("Gagal Konek Database"); 
        }
        
        return mysqlkonek;
    }
}