package com.campus.placement_portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.campus.placement_portal.entity.Student;
import com.campus.placement_portal.entity.Company;
import com.campus.placement_portal.repository.StudentRepository;
import com.campus.placement_portal.repository.CompanyRepository;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:3000")
public class LoginController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @PostMapping
    public LoginResponse login(@RequestBody LoginRequest request) {

        // Student Login
        if (request.getRole().equalsIgnoreCase("student")) {

            Student student = studentRepository.findByEmail(request.getEmail());

            if (student != null &&
                student.getPassword() != null &&
                student.getPassword().equals(request.getPassword())) {

                return new LoginResponse(
                        true,
                        "Student Login Successful",
                        "student",
                        student.getId()
                );
            }
        }

        // Company Login
        else if (request.getRole().equalsIgnoreCase("company")) {

            Company company = companyRepository.findByEmail(request.getEmail());

            if (company != null &&
                company.getPassword() != null &&
                company.getPassword().equals(request.getPassword())) {

                return new LoginResponse(
                        true,
                        "Company Login Successful",
                        "company",
                        company.getId()
                );
            }
        }

        return new LoginResponse(
                false,
                "Invalid Email or Password",
                null,
                null
        );
    }


    // Login Request
    public static class LoginRequest {

        private String email;
        private String password;
        private String role;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }


    // Login Response
    public static class LoginResponse {

        private boolean success;
        private String message;
        private String role;
        private Long userId;

        public LoginResponse(
                boolean success,
                String message,
                String role,
                Long userId) {

            this.success = success;
            this.message = message;
            this.role = role;
            this.userId = userId;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getRole() {
            return role;
        }

        public Long getUserId() {
            return userId;
        }
    }
}
