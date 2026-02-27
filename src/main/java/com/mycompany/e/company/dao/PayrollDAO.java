package com.mycompany.e.company.dao;

import com.mycompany.e.company.config.koneksi;
import com.mycompany.e.company.model.Payroll;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class PayrollDAO {

    // Ambil History Gaji Perusahaan
    public static ObservableList<Payroll> getPayrollByCompany(int companyId) {
        ObservableList<Payroll> list = FXCollections.observableArrayList();
        String sql = "SELECT p.id, e.full_name, p.bulan_tahun, p.gaji_pokok, p.potongan_bpjs + p.potongan_pajak as total_potongan, p.total_gaji_bersih, p.status " +
                "FROM payrolls p " +
                "JOIN employees e ON p.employee_id = e.employee_id " +
                "WHERE e.company_id = ?";

        try {
            Connection conn = koneksi.configDB();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, companyId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(new Payroll(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("bulan_tahun"),
                        rs.getDouble("gaji_pokok"),
                        rs.getDouble("total_potongan"),
                        rs.getDouble("total_gaji_bersih"),
                        rs.getString("status")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Simpan Gaji Baru
    public static boolean savePayroll(int empId, String bulan, double gapok, double tunjangan, double lembur, double bpjs, double pajak, double total) {
        String sql = "INSERT INTO payrolls (employee_id, bulan_tahun, gaji_pokok, tunjangan, lembur, potongan_bpjs, potongan_pajak, total_gaji_bersih, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Lunas')";
        try {
            Connection conn = koneksi.configDB();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, empId);
            pst.setString(2, bulan);
            pst.setDouble(3, gapok);
            pst.setDouble(4, tunjangan);
            pst.setDouble(5, lembur);
            pst.setDouble(6, bpjs);
            pst.setDouble(7, pajak);
            pst.setDouble(8, total);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}