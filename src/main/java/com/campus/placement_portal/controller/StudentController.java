
package com.campus.placement_portal.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.campus.placement_portal.entity.Student;
import com.campus.placement_portal.service.StudentService;

@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {

    @Autowired
    private StudentService studentService;


    // =========================
    // SAVE STUDENT
    // =========================

    @PostMapping
    public Student saveStudent(@RequestBody Student student) {
        return studentService.saveStudent(student);
    }


    // =========================
    // GET ALL STUDENTS
    // =========================

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }


    // =========================
    // GET STUDENT BY ID
    // =========================

    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }


    // =========================
    // UPDATE STUDENT
    // =========================

    @PutMapping("/{id}")
    public Student updateStudent(
            @PathVariable Long id,
            @RequestBody Student student) {

        return studentService.updateStudent(id, student);
    }


    // =========================
    // CHANGE PASSWORD
    // =========================

    @PutMapping("/{id}/password")
    public String changePassword(
            @PathVariable Long id,
            @RequestBody PasswordChangeRequest request) {

        boolean changed =
                studentService.changePassword(
                        id,
                        request.getCurrentPassword(),
                        request.getNewPassword()
                );

        if (changed) {
            return "Password changed successfully";
        }

        return "Current password is incorrect";
    }


    // =========================
    // DELETE STUDENT
    // =========================

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }


    // ==================================================
    // PHONE OTP
    // ==================================================

    @PostMapping("/send-otp")
    public String sendPhoneOtp(
            @RequestBody PhoneOtpRequest request) {

        String otp =
                studentService.generateOtp(
                        request.getPhone()
                );

        return "OTP generated successfully: " + otp;
    }


    @PostMapping("/verify-otp")
    public String verifyPhoneOtp(
            @RequestBody PhoneOtpVerifyRequest request) {

        boolean verified =
                studentService.verifyOtp(
                        request.getPhone(),
                        request.getOtp()
                );

        if (verified) {
            return "OTP verified successfully";
        }

        return "Invalid OTP";
    }


    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestBody PhonePasswordResetRequest request) {

        boolean reset =
                studentService.resetPassword(
                        request.getPhone(),
                        request.getNewPassword()
                );

        if (reset) {
            return "Password reset successfully";
        }

        return "Password reset failed";
    }


    // ==================================================
    // EMAIL OTP - FORGOT PASSWORD
    // ==================================================

    @PostMapping("/forgot-password/send-otp")
    public Map<String, Object> sendEmailOtp(
            @RequestBody EmailOtpRequest request) {

        boolean sent =
                studentService.sendEmailOtp(
                        request.getEmail()
                );

        if (sent) {

            return Map.of(
                    "success", true,
                    "message",
                    "OTP sent successfully to your email"
            );
        }

        return Map.of(
                "success", false,
                "message",
                "Student not found with this email"
        );
    }


    @PostMapping("/forgot-password/verify-otp")
    public Map<String, Object> verifyEmailOtp(
            @RequestBody EmailOtpVerifyRequest request) {

        boolean verified =
                studentService.verifyEmailOtp(
                        request.getEmail(),
                        request.getOtp()
                );

        if (verified) {

            return Map.of(
                    "success", true,
                    "message",
                    "Email OTP verified successfully"
            );
        }

        return Map.of(
                "success", false,
                "message",
                "Invalid or expired OTP"
        );
    }


    @PostMapping("/forgot-password/reset")
    public Map<String, Object> resetPasswordByEmail(
            @RequestBody EmailPasswordResetRequest request) {

        boolean reset =
                studentService.resetPasswordByEmail(
                        request.getEmail(),
                        request.getOtp(),
                        request.getNewPassword()
                );

        if (reset) {

            return Map.of(
                    "success", true,
                    "message",
                    "Password reset successfully"
            );
        }

        return Map.of(
                "success", false,
                "message",
                "Invalid or expired OTP"
        );
    }


    // ==================================================
    // RESUME UPLOAD / REPLACE
    // ==================================================

    @PostMapping("/{id}/resume")
    public Student uploadResume(
            @PathVariable Long id,
            @RequestParam("resume") MultipartFile file)
            throws IOException {

        Student student =
                studentService.getStudentById(id);

        if (student == null) {
            throw new RuntimeException(
                    "Student not found"
            );
        }


        // Empty file check
        if (file.isEmpty()) {
            throw new RuntimeException(
                    "Resume file is empty"
            );
        }


        // 15 MB limit
        if (file.getSize() > 15 * 1024 * 1024) {
            throw new RuntimeException(
                    "Resume file size should be less than 15MB"
            );
        }


        // Original file name
        String originalFileName =
                file.getOriginalFilename();


        if (originalFileName == null ||
                originalFileName.trim().isEmpty()) {

            throw new RuntimeException(
                    "Invalid resume file name"
            );
        }


        // Security: remove path from filename
        originalFileName =
                Paths.get(originalFileName)
                        .getFileName()
                        .toString();


        // File extension validation
        String lowerFileName =
                originalFileName.toLowerCase();


        if (!lowerFileName.endsWith(".pdf") &&
                !lowerFileName.endsWith(".doc") &&
                !lowerFileName.endsWith(".docx")) {

            throw new RuntimeException(
                    "Only PDF, DOC and DOCX files are allowed"
            );
        }


        // Upload folder
        Path uploadDirectory =
                Paths.get("uploads/student-resumes");

        Files.createDirectories(uploadDirectory);


        // Delete old resume when replacing
        if (student.getResumePath() != null &&
                !student.getResumePath().isEmpty()) {

            try {

                Path oldFile =
                        Paths.get(student.getResumePath());

                Files.deleteIfExists(oldFile);

            } catch (Exception e) {

                System.out.println(
                        "Old resume delete nahi ho paya: "
                                + e.getMessage()
                );
            }
        }


        // New file name
        String fileName =
                id + "_" + originalFileName;


        Path filePath =
                uploadDirectory.resolve(fileName);


        // Save file
        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );


        // Save resume information in database
        student.setResumeFileName(
                originalFileName
        );

        student.setResumePath(
                filePath.toString()
        );


        return studentService.updateStudent(
                id,
                student
        );
    }


    // ==================================================
    // VIEW RESUME
    // ==================================================

    @GetMapping("/{id}/resume")
    public ResponseEntity<Resource> viewResume(
            @PathVariable Long id)
            throws IOException {

        Student student =
                studentService.getStudentById(id);


        if (student == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }


        if (student.getResumePath() == null ||
                student.getResumePath().isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        Path filePath =
                Paths.get(student.getResumePath());


        if (!Files.exists(filePath)) {
            return ResponseEntity
                    .notFound()
                    .build();
        }


        Resource resource =
                new UrlResource(
                        filePath.toUri()
                );


        if (!resource.exists() ||
                !resource.isReadable()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        // Detect file type
        String contentType =
                Files.probeContentType(filePath);


        if (contentType == null) {

            contentType =
                    MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }


        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                contentType
                        )
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                                student.getResumeFileName() +
                                "\""
                )
                .body(resource);
    }


    // ==================================================
    // DELETE RESUME
    // ==================================================

    @DeleteMapping("/{id}/resume")
    public ResponseEntity<Map<String, Object>> deleteResume(
            @PathVariable Long id)
            throws IOException {

        Student student =
                studentService.getStudentById(id);


        if (student == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        if (student.getResumePath() == null ||
                student.getResumePath().isEmpty()) {

            return ResponseEntity.ok(
                    Map.of(
                            "success", false,
                            "message",
                            "No resume found"
                    )
            );
        }


        // Delete physical file
        Path filePath =
                Paths.get(
                        student.getResumePath()
                );

        Files.deleteIfExists(filePath);


        // Remove resume information
        student.setResumeFileName(null);
        student.setResumePath(null);


        studentService.updateStudent(
                id,
                student
        );


        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message",
                        "Resume deleted successfully"
                )
        );
    }


    // ==================================================
    // REQUEST CLASSES
    // ==================================================

    public static class PasswordChangeRequest {

        private String currentPassword;
        private String newPassword;


        public String getCurrentPassword() {
            return currentPassword;
        }


        public void setCurrentPassword(
                String currentPassword) {

            this.currentPassword =
                    currentPassword;
        }


        public String getNewPassword() {
            return newPassword;
        }


        public void setNewPassword(
                String newPassword) {

            this.newPassword =
                    newPassword;
        }
    }


    public static class PhoneOtpRequest {

        private String phone;


        public String getPhone() {
            return phone;
        }


        public void setPhone(String phone) {
            this.phone = phone;
        }
    }


    public static class PhoneOtpVerifyRequest {

        private String phone;
        private String otp;


        public String getPhone() {
            return phone;
        }


        public void setPhone(String phone) {
            this.phone = phone;
        }


        public String getOtp() {
            return otp;
        }


        public void setOtp(String otp) {
            this.otp = otp;
        }
    }


    public static class PhonePasswordResetRequest {

        private String phone;
        private String newPassword;


        public String getPhone() {
            return phone;
        }


        public void setPhone(String phone) {
            this.phone = phone;
        }


        public String getNewPassword() {
            return newPassword;
        }


        public void setNewPassword(
                String newPassword) {

            this.newPassword =
                    newPassword;
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


        public void setNewPassword(
                String newPassword) {

            this.newPassword =
                    newPassword;
        }
    }
}


