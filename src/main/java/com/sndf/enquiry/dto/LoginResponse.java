package com.sndf.enquiry.dto;

public class LoginResponse {

    private String token;

    private String role;

    private String department;

    public LoginResponse(
            String token,
            String role,
            String department) {

        this.token = token;
        this.role = role;
        this.department = department;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public String getDepartment() {
        return department;
    }
}