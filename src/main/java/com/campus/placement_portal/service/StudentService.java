package com.campus.placement_portal.service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.campus.placement_portal.entity.Student;
import com.campus.placement_portal.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EmailService emailService;

    public Student saveStudent(Student student) {

        Student existingStudent =
                studentRepository.findByEmail(student.getEmail());

        if (existingStudent != null) {
            throw new RuntimeException("Email already registered");
        }

        if (student.getPhoneVerified() == null) {
            student.setPhoneVerified(false);
        }

        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student updateStudent(Long id, Student student) {

        Student existingStudent =
                studentRepository.findById(id).orElse(null);

        if (existingStudent == null) {
            return null;
        }

        existingStudent.setName(student.getName());
        existingStudent.setPhone(student.getPhone());
        existingStudent.setCollege(student.getCollege());
        existingStudent.setCourse(student.getCourse());

        return studentRepository.save(existingStudent);
    }

    public boolean changePassword(
            Long id,
            String currentPassword,
            String newPassword) {

        Student student =
                studentRepository.findById(id).orElse(null);

        if (student == null) {
            return false;
        }

        if (student.getPassword() == null ||
                !student.getPassword().equals(currentPassword)) {
            return false;
        }

        student.setPassword(newPassword);

        studentRepository.save(student);

        return true;
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    // =========================================================
    // EXISTING PHONE OTP - TWILIO KE LIYE
    // =========================================================

    public String generateOtp(String phone) {

        Student student =
                studentRepository.findByPhone(phone);

        if (student == null) {
            throw new RuntimeException(
                    "Student not found with this phone number"
            );
        }

        String otp = String.format(
                "%06d",
                new Random().nextInt(1000000)
        );

        student.setOtp(otp);
        student.setPhoneVerified(false);

        studentRepository.save(student);

        return otp;
    }

    public boolean verifyOtp(
            String phone,
            String otp) {

        Student student =
                studentRepository.findByPhone(phone);

        if (student == null) {
            return false;
        }

        if (student.getOtp() != null &&
                student.getOtp().equals(otp)) {

            student.setPhoneVerified(true);

            studentRepository.save(student);

            return true;
        }

        return false;
    }

    public boolean resetPassword(
            String phone,
            String newPassword) {

        Student student =
                studentRepository.findByPhone(phone);

        if (student == null) {
            return false;
        }

        if (student.getPhoneVerified() == null ||
                !student.getPhoneVerified()) {

            return false;
        }

        student.setPassword(newPassword);
        student.setOtp(null);
        student.setPhoneVerified(false);

        studentRepository.save(student);

        return true;
    }

    // =========================================================
    // FORGOT PASSWORD - EMAIL OTP
    // =========================================================

    public boolean sendEmailOtp(String email) {

        Student student =
                studentRepository.findByEmail(email);

        if (student == null) {
            return false;
        }

        String emailOtp = String.format(
                "%06d",
                new Random().nextInt(1000000)
        );

        // OTP 5 minutes ke liye valid
        long expiryTime =
                System.currentTimeMillis()
                + (5 * 60 * 1000);

        student.setEmailOtp(emailOtp);
        student.setEmailOtpExpiry(expiryTime);

        studentRepository.save(student);

        // Email send karo
        emailService.sendOtpEmail(email, emailOtp);

        return true;
    }

    public boolean verifyEmailOtp(
            String email,
            String otp) {

        Student student =
                studentRepository.findByEmail(email);

        if (student == null) {
            return false;
        }

        if (student.getEmailOtp() == null ||
                student.getEmailOtpExpiry() == null) {

            return false;
        }

        // OTP expire check
        if (System.currentTimeMillis()
                > student.getEmailOtpExpiry()) {

            student.setEmailOtp(null);
            student.setEmailOtpExpiry(null);

            studentRepository.save(student);

            return false;
        }

        // OTP match check
        if (!student.getEmailOtp().equals(otp)) {
            return false;
        }

        return true;
    }

    public boolean resetPasswordByEmail(
            String email,
            String otp,
            String newPassword) {

        Student student =
                studentRepository.findByEmail(email);

        if (student == null) {
            return false;
        }

        if (student.getEmailOtp() == null ||
                student.getEmailOtpExpiry() == null) {

            return false;
        }

        // Expiry check
        if (System.currentTimeMillis()
                > student.getEmailOtpExpiry()) {

            student.setEmailOtp(null);
            student.setEmailOtpExpiry(null);

            studentRepository.save(student);

            return false;
        }

        // OTP check
        if (!student.getEmailOtp().equals(otp)) {
            return false;
        }

        // Password update
        student.setPassword(newPassword);

        // OTP invalidate
        student.setEmailOtp(null);
        student.setEmailOtpExpiry(null);

        studentRepository.save(student);

        return true;
    }
}