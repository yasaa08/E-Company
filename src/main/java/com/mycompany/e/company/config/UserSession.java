package com.mycompany.e.company.config;

public class UserSession {
    
    private static int userId;
    private static int companyId;
    private static String username;
    private static String role;
    private static int employeeId;


    public static void setSession(int uId, int cId, String user, String r, int empId) {
        userId = uId;
        companyId = cId;
        username = user;
        role = r;
        employeeId = empId;
    }


    public static int getEmployeeId() { return employeeId; }

    public static int getCompanyId() {
        return companyId;
    }
    
    public static String getUsername() {
        return username;
    }
    
    public static String getRole() {
        return role;
    }
}