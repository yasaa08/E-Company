package com.mycompany.e.company.dao;

import com.mycompany.e.company.config.koneksi;
import com.mycompany.e.company.model.Leave;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LeaveDAO {


    public static ObservableList<Leave> getLeavesByCompany(int companyId) {
        ObservableList<Leave> list = FXCollections.observableArrayList();
        String sql = "SELECT l.leave_id, e.full_name, l.leave_type, l.start_date, l.end_date, l.reason, l.status " +
                "FROM leaves l JOIN employees e ON l.employee_id = e.employee_id " +
                "WHERE e.company_id = ? ORDER BY l.leave_id DESC";

        try (Connection conn = koneksi.configDB();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, companyId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(new Leave(
                        rs.getInt("leave_id"), // Ambil dari kolom leave_id
                        rs.getString("full_name"),
                        rs.getString("leave_type"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getString("reason"),
                        rs.getString("status")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Update status (Approve / Reject)
    public static boolean updateLeaveStatus(int leaveId, String status) {
        // PERHATIKAN: WHERE id = ? diganti menjadi WHERE leave_id = ?
        String sql = "UPDATE leaves SET status = ? WHERE leave_id = ?";
        try (Connection conn = koneksi.configDB();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, status);
            pst.setInt(2, leaveId);
            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String ajukanCuti(int employeeId, String tipeCuti, java.time.LocalDate startDate, java.time.LocalDate endDate, String alasan) {
        String sql = "INSERT INTO leaves (employee_id, leave_type, start_date, end_date, reason, status) VALUES (?, ?, ?, ?, ?, 'Pending')";

        try {
            Connection conn = koneksi.configDB();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(1, employeeId);
            pst.setString(2, tipeCuti);
            pst.setDate(3, java.sql.Date.valueOf(startDate));
            pst.setDate(4, java.sql.Date.valueOf(endDate));
            pst.setString(5, alasan);

            pst.executeUpdate();
            return "SUKSES"; // Kalau berhasil, kembalikan kata SUKSES

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage(); // Kalau gagal, kembalikan pesan error aslinya!
        }
    }
}