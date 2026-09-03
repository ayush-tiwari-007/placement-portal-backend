
package com.campus.placement_portal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "student_seq"
    )
    @SequenceGenerator(
        name = "student_seq",
        sequenceName = "student_seq",
        allocationSize = 1
    )
    private Long id;

    private String name;

    private String email;

    private String phone;

    // Existing Phone OTP - Twilio ke liye
    private String otp;

    // Phone OTP verification
    private Boolean phoneVerified = false;

    // Forgot Password - Email OTP
    private String emailOtp;

    // Email OTP expiry timestamp
    private Long emailOtpExpiry;

    private String college;

    private String course;

    private String password;

    // =========================
    // RESUME
    // =========================

    private String resumeFileName;

    private String resumePath;


    // =========================
    // DEFAULT CONSTRUCTOR
    // =========================

    public Student() {

    }


    // =========================
    // PARAMETERIZED CONSTRUCTOR
    // =========================

    public Student(
            Long id,
            String name,
            String email,
            String phone,
            String otp,
            Boolean phoneVerified,
            String emailOtp,
            Long emailOtpExpiry,
            String college,
            String course,
            String password) {

        this.id = id;

        this.name = name;

        this.email = email;

        this.phone = phone;

        this.otp = otp;

        this.phoneVerified = phoneVerified;

        this.emailOtp = emailOtp;

        this.emailOtpExpiry = emailOtpExpiry;

        this.college = college;

        this.course = course;

        this.password = password;

    }


    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() {

        return id;

    }

    public void setId(Long id) {

        this.id = id;

    }


    public String getName() {

        return name;

    }

    public void setName(String name) {

        this.name = name;

    }


    public String getEmail() {

        return email;

    }

    public void setEmail(String email) {

        this.email = email;

    }


    public String getPhone() {

        return phone;

    }

    public void setPhone(String phone) {

        this.phone = phone;

    }


    // =========================
    // PHONE OTP
    // =========================

    public String getOtp() {

        return otp;

    }

    public void setOtp(String otp) {

        this.otp = otp;

    }


    public Boolean getPhoneVerified() {

        return phoneVerified;

    }

    public void setPhoneVerified(Boolean phoneVerified) {

        this.phoneVerified = phoneVerified;

    }


    // =========================
    // EMAIL OTP
    // =========================

    public String getEmailOtp() {

        return emailOtp;

    }

    public void setEmailOtp(String emailOtp) {

        this.emailOtp = emailOtp;

    }


    public Long getEmailOtpExpiry() {

        return emailOtpExpiry;

    }

    public void setEmailOtpExpiry(Long emailOtpExpiry) {

        this.emailOtpExpiry = emailOtpExpiry;

    }


    public String getCollege() {

        return college;

    }

    public void setCollege(String college) {

        this.college = college;

    }


    public String getCourse() {

        return course;

    }

    public void setCourse(String course) {

        this.course = course;

    }


    public String getPassword() {

        return password;

    }

    public void setPassword(String password) {

        this.password = password;

    }


    // =========================
    // RESUME GETTERS & SETTERS
    // =========================

    public String getResumeFileName() {

        return resumeFileName;

    }

    public void setResumeFileName(String resumeFileName) {

        this.resumeFileName = resumeFileName;

    }


    public String getResumePath() {

        return resumePath;

    }

    public void setResumePath(String resumePath) {

        this.resumePath = resumePath;

    }

}
