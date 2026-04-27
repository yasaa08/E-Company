package com.mycompany.e.company.config; // Sesuaikan dengan package kamu

import com.mycompany.e.company.config.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class UpdateBanyakUMR {

    public static void main(String[] args) {
        try {
            System.out.println("Menghubungkan ke Cloud Database...");
            Connection conn = koneksi.configDB();
            Statement stmt = conn.createStatement();

            // 1. HAPUS SEMUA DATA LAMA (Membersihkan Tabel)
            System.out.println("Membersihkan data kota lama...");
            // Menggunakan TRUNCATE atau DELETE.
            // Kita gunakan DELETE agar aman jika ada Foreign Key.
            stmt.executeUpdate("DELETE FROM regions");

            // Reset Auto Increment agar ID mulai dari 1 lagi
            stmt.executeUpdate("ALTER TABLE regions AUTO_INCREMENT = 1");

            // 2. SIAPKAN DATA BARU (Data dari kamu)
            String sqlInsert = "INSERT INTO regions (region_name, umr_value) VALUES (?, ?)";
            PreparedStatement pst = conn.prepareStatement(sqlInsert);

            Object[][] dataBaru = {
                    {"DKI Jakarta", 5396761},
                    {"Kota Bekasi", 5690752},
                    {"Kabupaten Karawang", 5599593},
                    {"Kota Depok", 5195721},
                    {"Kota Bogor", 5126897},
                    {"Kota Tangerang", 5069708},
                    {"Kota Tangerang Selatan", 4974392},
                    {"Kota Bandung", 4482914},
                    {"Kabupaten Bandung", 3757284},
                    {"Kota Semarang", 3454827},
                    {"Kota Surabaya", 4961753},
                    {"Kabupaten Sidoarjo", 4870511},
                    {"Kabupaten Gresik", 4874133},
                    {"Kota Malang", 3507693},
                    {"Kota Cirebon", 2697685},
                    {"Kota Denpasar", 3298116}
            };

            System.out.println("Memasukkan " + dataBaru.length + " data kota terbaru...");

            for (Object[] kota : dataBaru) {
                pst.setString(1, (String) kota[0]);
                pst.setDouble(2, ((Number) kota[1]).doubleValue());
                pst.addBatch();
            }

            pst.executeBatch();

            System.out.println("🎉 BERHASIL! Data lama telah dihapus dan diganti dengan data terbaru.");
            System.out.println("Sekarang silakan restart aplikasi utama kamu.");

        } catch (Exception e) {
            System.err.println("Gagal mereset data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}