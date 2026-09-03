
package com.campus.placement_portal.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.campus.placement_portal.entity.Company;
import com.campus.placement_portal.service.CompanyService;

@RestController
@RequestMapping("/companies")
@CrossOrigin(origins = "http://localhost:3000")
public class CompanyController {

    @Autowired
    private CompanyService companyService;


    // =========================================================
    // EXISTING COMPANY APIs
    // =========================================================

    @GetMapping
    public List<Company> getAllCompanies() {

        return companyService.getAllCompanies();

    }


    @PostMapping
    public Company addCompany(@RequestBody Company company) {

        return companyService.saveCompany(company);

    }


    @GetMapping("/{id}")
    public Company getCompanyById(@PathVariable Long id) {

        return companyService.getCompanyById(id);

    }


    @PutMapping("/{id}")
    public Company updateCompany(
            @PathVariable Long id,
            @RequestBody Company company) {

        return companyService.updateCompany(id, company);

    }


    @PutMapping("/{id}/password")
    public String changePassword(
            @PathVariable Long id,
            @RequestBody PasswordChangeRequest request) {

        boolean changed =
                companyService.changePassword(
                        id,
                        request.getCurrentPassword(),
                        request.getNewPassword()
                );

        if (changed) {

            return "Password changed successfully";

        }

        return "Current password is incorrect";

    }


    @DeleteMapping("/{id}")
    public String deleteCompany(@PathVariable Long id) {

        companyService.deleteCompany(id);

        return "Company Deleted Successfully";

    }


    // =========================================================
    // FORGOT PASSWORD - EMAIL OTP
    // =========================================================

    @PostMapping("/forgot-password/send-otp")
    public Map<String, Object> sendResetOtp(
            @RequestBody EmailOtpRequest request) {

        boolean sent =
                companyService.sendResetOtp(
                        request.getEmail()
                );

        if (sent) {

            return Map.of(
                    "success", true,
                    "message", "OTP sent successfully to your email"
            );

        }

        return Map.of(
                "success", false,
                "message", "Company not found with this email"
        );

    }


    @PostMapping("/forgot-password/verify-otp")
    public Map<String, Object> verifyResetOtp(
            @RequestBody EmailOtpVerifyRequest request) {

        boolean verified =
                companyService.verifyResetOtp(
                        request.getEmail(),
                        request.getOtp()
                );

        if (verified) {

            return Map.of(
                    "success", true,
                    "message", "Email OTP verified successfully"
            );

        }

        return Map.of(
                "success", false,
                "message", "Invalid or expired OTP"
        );

    }


    @PostMapping("/forgot-password/reset")
    public Map<String, Object> resetPasswordByEmail(
            @RequestBody EmailPasswordResetRequest request) {

        boolean reset =
                companyService.resetPasswordByEmail(
                        request.getEmail(),
                        request.getOtp(),
                        request.getNewPassword()
                );

        if (reset) {

            return Map.of(
                    "success", true,
                    "message", "Password reset successfully"
            );

        }

        return Map.of(
                "success", false,
                "message", "Invalid or expired OTP"
        );

    }


    // =========================================================
    // REQUEST CLASSES
    // =========================================================

    public static class PasswordChangeRequest {

        private String currentPassword;
        private String newPassword;


        public String getCurrentPassword() {

            return currentPassword;

        }


        public void setCurrentPassword(String currentPassword) {

            this.currentPassword = currentPassword;

        }


        public String getNewPassword() {

            return newPassword;

        }


        public void setNewPassword(String newPassword) {

            this.newPassword = newPassword;

        }

    }


    public static class EmailOtpRequest {

        private String email;


        public String getEmail() {

            return email;

        }


        public void setEmail(String email) {

            this.email = email;

        }

    }


    public static class EmailOtpVerifyRequest {

        private String email;
        private String otp;


        public String getEmail() {

            return email;

        }


        public void setEmail(String email) {

            this.email = email;

        }


        public String getOtp() {

            return otp;

        }


        public void setOtp(String otp) {

            this.otp = otp;

        }

    }


    public static class EmailPasswordResetRequest {

        private String email;
        private String otp;
        private String newPassword;


        public String getEmail() {

            return email;

        }


        public void setEmail(String email) {

            this.email = email;

        }


        public String getOtp() {

            return otp;

        }


        public void setOtp(String otp) {

            this.otp = otp;

        }


        public String getNewPassword() {

            return newPassword;

        }


        public void setNewPassword(String newPassword) {

            this.newPassword = newPassword;

        }

    }

}
