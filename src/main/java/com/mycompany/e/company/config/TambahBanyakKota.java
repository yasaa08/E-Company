package com.mycompany.e.company.config; // Sesuaikan dengan nama package kamu

import com.mycompany.e.company.config.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class TambahBanyakKota {

    public static void main(String[] args) {
        String sql = "INSERT INTO regions (region_name, umr_value) VALUES (?, ?)";

        try {
            System.out.println("Menghubungkan ke Cloud Database...");
            Connection conn = koneksi.configDB();
            PreparedStatement pst = conn.prepareStatement(sql);

            // Array Data: { "Nama Kota", UMR_Value }
            Object[][] dataKota = {
                    {"DKI Jakarta", 5067381},
                    {"Kota Bekasi", 5343430},
                    {"Kabupaten Karawang", 5257834},
                    {"Kota Depok", 4878612},
                    {"Kota Bogor", 4813988},
                    {"Kota Tangerang", 4760289},
                    {"Kota Tangerang Selatan", 4670791},
                    {"Kota Bandung", 4209309},
                    {"Kabupaten Bandung", 3527967},
                    {"Kota Semarang", 3243969},
                    {"Kota Surabaya", 4725479},
                    {"Kabupaten Sidoarjo", 4638582},
                    {"Kabupaten Gresik", 4642031},
                    {"Kota Malang", 3309144},
                    {"Kota Surakarta (Solo)", 2269070},
                    {"Kota Yogyakarta", 2492997},
                    {"Kota Cirebon", 2533038},
                    {"Kota Medan", 3769082},
                    {"Kota Batam", 4685050},
                    {"Kota Palembang", 3677591},
                    {"Kota Pekanbaru", 3451584},
                    {"Kota Bandar Lampung", 3103631},
                    {"Kota Makassar", 3643321},
                    {"Kota Manado", 3590000},
                    {"Kota Balikpapan", 3475595},
                    {"Kota Samarinda", 3497124},
                    {"Kota Banjarmasin", 3379513},
                    {"Kota Pontianak", 2840206},
                    {"Kota Denpasar", 3227016},
                    {"Kota Mataram", 2685089}
            };

            System.out.println("Memproses " + dataKota.length + " Kota...");

            // Looping untuk memasukkan data ke antrean (Batch)
            for (Object[] kota : dataKota) {
                pst.setString(1, (String) kota[0]);
                // Konversi aman dari Number ke Double
                pst.setDouble(2, ((Number) kota[1]).doubleValue());
                pst.addBatch(); // Masukkan ke antrean
            }

            // Tembakkan semua antrean ke database sekaligus!
            int[] hasil = pst.executeBatch();

            System.out.println("🎉 BERHASIL! " + hasil.length + " Kota dan UMR-nya telah ditambahkan ke database!");
            System.out.println("Kamu bisa menghapus file ini sekarang.");

        } catch (Exception e) {
            System.err.println("Gagal menambahkan kota: " + e.getMessage());
            e.printStackTrace();
        }
    }
}