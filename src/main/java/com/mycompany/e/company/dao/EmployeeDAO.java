package com.mycompany.e.company.dao;

import com.mycompany.e.company.config.koneksi;
import com.mycompany.e.company.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;

public class EmployeeDAO {

    public static ObservableList<Employee> getEmployeesByCompany(int companyId) {
        ObservableList<Employee> list = FXCollections.observableArrayList();
        String sql = "SELECT e.employee_id, e.full_name, e.position, e.marital_status, " +
                "e.base_salary, e.join_date, e.child_count, r.region_name " +
                "FROM employees e " +
                "LEFT JOIN regions r ON e.region_id = r.region_id " +
                "WHERE e.company_id = ?";

        try {
            Connection conn = koneksi.configDB();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, companyId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("position"),
                        rs.getString("marital_status"),
                        rs.getDouble("base_salary"),
                        rs.getString("region_name"),
                        rs.getDate("join_date"),
                        rs.getInt("child_count")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addEmployee(String nama, String jabatan, String status, double gaji, int regionId, int companyId, LocalDate joinDate) {
        String sql = "INSERT INTO employees (full_name, position, marital_status, base_salary, region_id, company_id, join_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = koneksi.configDB();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, nama);
            pst.setString(2, jabatan);
            pst.setString(3, status);
            pst.setDouble(4, gaji);
            pst.setInt(5, regionId);
            pst.setInt(6, companyId);

            // --- PERBAIKAN DI SINI ---
            pst.setDate(7, Date.valueOf(joinDate)); // Ganti 72 jadi 7
            // -------------------------

            int rows = pst.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}