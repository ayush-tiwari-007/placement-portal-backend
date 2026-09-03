package com.campus.placement_portal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "company_seq"
    )
    @SequenceGenerator(
            name = "company_seq",
            sequenceName = "company_seq",
            allocationSize = 1
    )
    private Long id;

    private String companyName;
    private String email;
    private String location;
    private String website;
    private String password;

    // Existing OTP - Email verification ke liye
    private String otp;

    // Existing OTP expiry
    private Long otpExpiry;

    // Email verification status
    private Boolean emailVerified = false;

    // Forgot Password - separate Email OTP
    private String resetOtp;

    // Reset OTP expiry timestamp
    private Long resetOtpExpiry;

    // Reset OTP verification status
    private Boolean resetOtpVerified = false;

    // Default Constructor
    public Company() {
    }

    // Parameterized Constructor
    public Company(
            Long id,
            String companyName,
            String email,
            String location,
            String website,
            String password,
            String otp,
            Long otpExpiry,
            Boolean emailVerified,
            String resetOtp,
            Long resetOtpExpiry,
            Boolean resetOtpVerified) {

        this.id = id;
        this.companyName = companyName;
        this.email = email;
        this.location = location;
        this.website = website;
        this.password = password;
        this.otp = otp;
        this.otpExpiry = otpExpiry;
        this.emailVerified = emailVerified;
        this.resetOtp = resetOtp;
        this.resetOtpExpiry = resetOtpExpiry;
        this.resetOtpVerified = resetOtpVerified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Existing OTP
    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Long getOtpExpiry() {
        return otpExpiry;
    }

    public void setOtpExpiry(Long otpExpiry) {
        this.otpExpiry = otpExpiry;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    // Forgot Password OTP
    public String getResetOtp() {
        return resetOtp;
    }

    public void setResetOtp(String resetOtp) {
        this.resetOtp = resetOtp;
    }

    public Long getResetOtpExpiry() {
        return resetOtpExpiry;
    }

    public void setResetOtpExpiry(Long resetOtpExpiry) {
        this.resetOtpExpiry = resetOtpExpiry;
    }

    public Boolean getResetOtpVerified() {
        return resetOtpVerified;
    }

    public void setResetOtpVerified(Boolean resetOtpVerified) {
        this.resetOtpVerified = resetOtpVerified;
    }
}

