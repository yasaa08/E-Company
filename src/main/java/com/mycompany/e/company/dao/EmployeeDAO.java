package com.mycompany.e.company.dao;

import com.mycompany.e.company.config.koneksi;
import com.mycompany.e.company.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.Statement; // Wajib import ini untuk ambil ID otomatis
import java.time.LocalDate;

public class EmployeeDAO {

    public static ObservableList<Employee> getEmployeesByCompany(int companyId) {
        ObservableList<Employee> list = FXCollections.observableArrayList();
        String sql = "SELECT e.employee_id, e.full_name, e.position, e.marital_status, " +
                "e.base_salary, e.join_date, e.child_count, r.region_name, " +
                "e.account_number, e.phone_number " +
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
                        rs.getInt("child_count"),
                        rs.getString("account_number"),
                        rs.getString("phone_number")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addEmployee(String nama, String jabatan, String status, double gaji, int regionId, int companyId, LocalDate joinDate, String noRekening, String noHp) {

        String sql = "INSERT INTO employees (full_name, position, marital_status, base_salary, region_id, company_id, join_date, account_number, phone_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = koneksi.configDB();

            // PERUBAHAN PENTING: Minta database mengembalikan ID karyawan yang baru saja dibuat
            PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pst.setString(1, nama);
            pst.setString(2, jabatan);
            pst.setString(3, status);
            pst.setDouble(4, gaji);
            pst.setInt(5, regionId);
            pst.setInt(6, companyId);
            pst.setDate(7, Date.valueOf(joinDate));
            pst.setString(8, noRekening);
            pst.setString(9, noHp); // Ini Nomor WA

            int rows = pst.executeUpdate();

            // JIKA SUKSES MENYIMPAN DATA KARYAWAN:
            if (rows > 0) {
                ResultSet rsKeys = pst.getGeneratedKeys();
                if (rsKeys.next()) {
                    int newEmployeeId = rsKeys.getInt(1); // Ambil ID Karyawan yang baru

                    // OTOMATIS BUAT AKUN LOGIN KARYAWAN
                    // Username = Nomor WA, Password = "123456", Role = "employee"
                    String sqlUser = "INSERT INTO users (username, password, role, company_id, employee_id) VALUES (?, '123456', 'employee', ?, ?)";
                    PreparedStatement pstUser = conn.prepareStatement(sqlUser);
                    pstUser.setString(1, noHp);
                    pstUser.setInt(2, companyId);
                    pstUser.setInt(3, newEmployeeId);

                    pstUser.executeUpdate();
                }
                return true;
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fungsi Update (Biarkan sama)
    public static boolean updateEmployee(int empId, String nama, String jabatan, String status, double gaji, int regionId, LocalDate joinDate, String noRekening, String noHp) {
        String sql = "UPDATE employees SET full_name=?, position=?, marital_status=?, base_salary=?, region_id=?, join_date=?, account_number=?, phone_number=? WHERE employee_id=?";
        try {
            Connection conn = koneksi.configDB();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, nama);
            pst.setString(2, jabatan);
            pst.setString(3, status);
            pst.setDouble(4, gaji);
            pst.setInt(5, regionId);
            pst.setDate(6, Date.valueOf(joinDate));
            pst.setString(7, noRekening);
            pst.setString(8, noHp);
            pst.setInt(9, empId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fungsi Delete (Biarkan sama)
    public static boolean deleteEmployee(int empId) {
        String sql = "DELETE FROM employees WHERE employee_id=?";
        try {
            Connection conn = koneksi.configDB();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, empId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}