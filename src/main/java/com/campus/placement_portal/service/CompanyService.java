package com.campus.placement_portal.service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.campus.placement_portal.entity.Company;
import com.campus.placement_portal.repository.CompanyRepository;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private EmailService emailService;

    // =========================================================
    // EXISTING COMPANY METHODS
    // =========================================================

    public Company saveCompany(Company company) {

        Company existingCompany =
                companyRepository.findByEmail(company.getEmail());

        if (existingCompany != null) {
            throw new RuntimeException("Email already registered");
        }

        if (company.getEmailVerified() == null) {
            company.setEmailVerified(false);
        }

        if (company.getResetOtpVerified() == null) {
            company.setResetOtpVerified(false);
        }

        return companyRepository.save(company);
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Company getCompanyById(Long id) {
        return companyRepository.findById(id).orElse(null);
    }

    public Company updateCompany(
            Long id,
            Company company) {

        Company existingCompany =
                companyRepository.findById(id).orElse(null);

        if (existingCompany == null) {
            return null;
        }

        existingCompany.setCompanyName(company.getCompanyName());
        existingCompany.setLocation(company.getLocation());
        existingCompany.setWebsite(company.getWebsite());

        return companyRepository.save(existingCompany);
    }

    public boolean changePassword(
            Long id,
            String currentPassword,
            String newPassword) {

        Company company =
                companyRepository.findById(id).orElse(null);

        if (company == null) {
            return false;
        }

        if (company.getPassword() == null ||
                !company.getPassword().equals(currentPassword)) {

            return false;
        }

        company.setPassword(newPassword);

        companyRepository.save(company);

        return true;
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    // =========================================================
    // FORGOT PASSWORD - EMAIL OTP
    // =========================================================

    public boolean sendResetOtp(String email) {

        Company company =
                companyRepository.findByEmail(email);

        if (company == null) {
            return false;
        }

        // 6 digit OTP
        String otp = String.format(
                "%06d",
                new Random().nextInt(1000000)
        );

        // OTP 5 minutes ke liye valid
        long expiryTime =
                System.currentTimeMillis()
                + (5 * 60 * 1000);

        company.setResetOtp(otp);
        company.setResetOtpExpiry(expiryTime);
        company.setResetOtpVerified(false);

        companyRepository.save(company);

        // Email send
        emailService.sendOtpEmail(email, otp);

        return true;
    }

    public boolean verifyResetOtp(
            String email,
            String otp) {

        Company company =
                companyRepository.findByEmail(email);

        if (company == null) {
            return false;
        }

        if (company.getResetOtp() == null ||
                company.getResetOtpExpiry() == null) {

            return false;
        }

        // Expiry check
        if (System.currentTimeMillis()
                > company.getResetOtpExpiry()) {

            company.setResetOtp(null);
            company.setResetOtpExpiry(null);
            company.setResetOtpVerified(false);

            companyRepository.save(company);

            return false;
        }

        // OTP match
        if (!company.getResetOtp().equals(otp)) {
            return false;
        }

        // OTP verified
        company.setResetOtpVerified(true);

        companyRepository.save(company);

        return true;
    }

    public boolean resetPasswordByEmail(
            String email,
            String otp,
            String newPassword) {

        Company company =
                companyRepository.findByEmail(email);

        if (company == null) {
            return false;
        }

        if (company.getResetOtp() == null ||
                company.getResetOtpExpiry() == null) {

            return false;
        }

        // Expiry check
        if (System.currentTimeMillis()
                > company.getResetOtpExpiry()) {

            company.setResetOtp(null);
            company.setResetOtpExpiry(null);
            company.setResetOtpVerified(false);

            companyRepository.save(company);

            return false;
        }

        // OTP match
        if (!company.getResetOtp().equals(otp)) {
            return false;
        }

        // Password update
        company.setPassword(newPassword);

        // OTP invalidate
        company.setResetOtp(null);
        company.setResetOtpExpiry(null);
        company.setResetOtpVerified(false);

        companyRepository.save(company);

        return true;
    }
}