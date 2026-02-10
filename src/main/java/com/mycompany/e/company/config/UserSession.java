package com.mycompany.e.company.config;

public class UserSession {
    
    private static int userId;
    private static int companyId;
    private static String username;
    private static String role;
    

    public static void setSession(int uId, int cId, String user, String r) {
        userId = uId;
        companyId = cId;
        username = user;
        role = r;
    }
    

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