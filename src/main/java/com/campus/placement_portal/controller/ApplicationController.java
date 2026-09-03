package com.campus.placement_portal.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.campus.placement_portal.entity.Application;
import com.campus.placement_portal.entity.job;
import com.campus.placement_portal.repository.ApplicationRepository;
import com.campus.placement_portal.service.JobService;

@RestController
@RequestMapping("/applications")
@CrossOrigin(origins = "http://localhost:3000")
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobService jobService;

    // =========================================================
    // GET ALL APPLICATIONS
    // =========================================================

    @GetMapping
    public List<Application> getAllApplications() {

        return applicationRepository.findAll();
    }

    // =========================================================
    // GET STUDENT APPLICATIONS
    // =========================================================

    @GetMapping("/student/{studentId}")
    public List<Application> getStudentApplications(
            @PathVariable Long studentId) {

        return applicationRepository.findByStudentId(studentId);
    }

    // =========================================================
    // GET COMPANY APPLICATIONS
    // =========================================================

    @GetMapping("/company/{companyId}")
    public List<Application> getCompanyApplications(
            @PathVariable Long companyId) {

        return applicationRepository.findByCompanyId(companyId);
    }

    // =========================================================
    // APPLY FOR JOB
    // =========================================================

    @PostMapping
    public ResponseEntity<?> applyJob(
            @RequestBody Application application) {

        // -----------------------------------------------------
        // Validate Student ID
        // -----------------------------------------------------

        if (application.getStudentId() == null) {

            return ResponseEntity.badRequest()
                    .body("Student ID is required");
        }

        // -----------------------------------------------------
        // Validate Job ID
        // -----------------------------------------------------

        if (application.getJobId() == null) {

            return ResponseEntity.badRequest()
                    .body("Job ID is required");
        }

        // -----------------------------------------------------
        // Find Selected Job
        // -----------------------------------------------------

        job currentJob =
                jobService.getJobById(application.getJobId());

        if (currentJob == null) {

            return ResponseEntity.badRequest()
                    .body("Job not found");
        }

        // -----------------------------------------------------
        // Get Company ID From Job
        // -----------------------------------------------------

        Long companyId =
                currentJob.getCompanyId();

        if (companyId == null) {

            return ResponseEntity.badRequest()
                    .body(
                        "This job is not connected with any company"
                    );
        }

        // -----------------------------------------------------
        // Automatically Connect Application With Company
        // -----------------------------------------------------

        application.setCompanyId(companyId);

        // -----------------------------------------------------
        // Default Status
        // -----------------------------------------------------

        if (application.getStatus() == null ||
                application.getStatus().trim().isEmpty()) {

            application.setStatus("Applied");
        }

        // -----------------------------------------------------
        // Default Applied Date
        // -----------------------------------------------------

        if (application.getAppliedDate() == null ||
                application.getAppliedDate().trim().isEmpty()) {

            application.setAppliedDate(
                    java.time.LocalDate.now().toString()
            );
        }

        // -----------------------------------------------------
        // Default Blacklist Value
        // -----------------------------------------------------

        if (application.getBlacklisted() == null) {

            application.setBlacklisted(false);
        }

        // =====================================================
        // BLACKLIST PROTECTION
        // =====================================================
        //
        // IMPORTANT:
        // We are NOT using existsBy... here because Oracle was
        // generating "fetch first ? rows only" and giving ORA-00933.
        //
        // Instead we fetch matching blacklisted applications and
        // check whether the list is empty.
        // =====================================================

        List<Application> blacklistedApplications =
                applicationRepository
                        .findByStudentIdAndCompanyIdAndBlacklistedTrue(
                                application.getStudentId(),
                                companyId
                        );

        if (!blacklistedApplications.isEmpty()) {

            return ResponseEntity.status(403)
                    .body(
                        "You are blacklisted by this company and cannot apply for its jobs."
                    );
        }

        // =====================================================
        // SAVE APPLICATION
        // =====================================================

        Application savedApplication =
                applicationRepository.save(application);

        return ResponseEntity.ok(savedApplication);
    }

    // =========================================================
    // GET APPLICATION BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getApplicationById(
            @PathVariable Long id) {

        Application application =
                applicationRepository
                        .findById(id)
                        .orElse(null);

        if (application == null) {

            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(application);
    }

    // =========================================================
    // UPDATE APPLICATION
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateApplication(
            @PathVariable Long id,
            @RequestBody Application application) {

        Application existingApplication =
                applicationRepository
                        .findById(id)
                        .orElse(null);

        if (existingApplication == null) {

            return ResponseEntity.notFound().build();
        }

        // -----------------------------------------------------
        // Student ID
        // -----------------------------------------------------

        if (application.getStudentId() != null) {

            existingApplication.setStudentId(
                    application.getStudentId()
            );
        }

        // -----------------------------------------------------
        // Job ID
        // -----------------------------------------------------

        if (application.getJobId() != null) {

            existingApplication.setJobId(
                    application.getJobId()
            );
        }

        // -----------------------------------------------------
        // Status
        // -----------------------------------------------------

        if (application.getStatus() != null) {

            existingApplication.setStatus(
                    application.getStatus()
            );
        }

        // -----------------------------------------------------
        // Applied Date
        // -----------------------------------------------------

        if (application.getAppliedDate() != null) {

            existingApplication.setAppliedDate(
                    application.getAppliedDate()
            );
        }

        // -----------------------------------------------------
        // Resume File Name
        // -----------------------------------------------------

        if (application.getResumeFileName() != null) {

            existingApplication.setResumeFileName(
                    application.getResumeFileName()
            );
        }

        // -----------------------------------------------------
        // Resume Path
        // -----------------------------------------------------

        if (application.getResumePath() != null) {

            existingApplication.setResumePath(
                    application.getResumePath()
            );
        }

        // -----------------------------------------------------
        // Company ID
        // -----------------------------------------------------

        if (application.getCompanyId() != null) {

            existingApplication.setCompanyId(
                    application.getCompanyId()
            );
        }

        // -----------------------------------------------------
        // Blacklisted
        // -----------------------------------------------------

        if (application.getBlacklisted() != null) {

            existingApplication.setBlacklisted(
                    application.getBlacklisted()
            );
        }

        // -----------------------------------------------------
        // Blacklist Reason
        // -----------------------------------------------------

        if (application.getBlacklistReason() != null) {

            existingApplication.setBlacklistReason(
                    application.getBlacklistReason()
            );
        }

        return ResponseEntity.ok(
                applicationRepository.save(
                        existingApplication
                )
        );
    }

    // =========================================================
    // SCHEDULE INTERVIEW
    // =========================================================

    @PutMapping("/{id}/interview")
    public ResponseEntity<?> scheduleInterview(
            @PathVariable Long id,
            @RequestBody Application interviewData) {

        Application application =
                applicationRepository
                        .findById(id)
                        .orElse(null);

        if (application == null) {

            return ResponseEntity.notFound().build();
        }

        // -----------------------------------------------------
        // Interview Date
        // -----------------------------------------------------

        application.setInterviewDate(
                interviewData.getInterviewDate()
        );

        // -----------------------------------------------------
        // Interview Time
        // -----------------------------------------------------

        application.setInterviewTime(
                interviewData.getInterviewTime()
        );

        // -----------------------------------------------------
        // Interview Mode
        // -----------------------------------------------------

        application.setInterviewMode(
                interviewData.getInterviewMode()
        );

        // -----------------------------------------------------
        // Interview Round
        // -----------------------------------------------------

        application.setInterviewRound(
                interviewData.getInterviewRound()
        );

        // -----------------------------------------------------
        // Meeting Link
        // -----------------------------------------------------

        application.setMeetingLink(
                interviewData.getMeetingLink()
        );

        // -----------------------------------------------------
        // Interview Venue
        // -----------------------------------------------------

        application.setInterviewVenue(
                interviewData.getInterviewVenue()
        );

        // -----------------------------------------------------
        // Interview Instructions
        // -----------------------------------------------------

        application.setInterviewInstructions(
                interviewData.getInterviewInstructions()
        );

        // -----------------------------------------------------
        // Update Status
        // -----------------------------------------------------

        application.setStatus(
                "Interview Scheduled"
        );

        return ResponseEntity.ok(
                applicationRepository.save(application)
        );
    }

    // =========================================================
    // SELECT CANDIDATE
    // =========================================================

    @PutMapping("/{id}/select")
    public ResponseEntity<?> selectCandidate(
            @PathVariable Long id) {

        Application application =
                applicationRepository
                        .findById(id)
                        .orElse(null);

        if (application == null) {

            return ResponseEntity.notFound().build();
        }

        application.setStatus("Selected");

        return ResponseEntity.ok(
                applicationRepository.save(application)
        );
    }

    // =========================================================
    // BLACKLIST CANDIDATE
    // =========================================================

    @PutMapping("/{id}/blacklist")
    public ResponseEntity<?> blacklistCandidate(
            @PathVariable Long id,
            @RequestBody BlacklistRequest request) {

        Application application =
                applicationRepository
                        .findById(id)
                        .orElse(null);

        if (application == null) {

            return ResponseEntity.notFound().build();
        }

        // -----------------------------------------------------
        // Set Blacklisted
        // -----------------------------------------------------

        application.setBlacklisted(true);

        // -----------------------------------------------------
        // Save Reason
        // -----------------------------------------------------

        if (request != null) {

            application.setBlacklistReason(
                    request.getReason()
            );
        }

        // -----------------------------------------------------
        // Update Status
        // -----------------------------------------------------

        application.setStatus("Blacklisted");

        return ResponseEntity.ok(
                applicationRepository.save(application)
        );
    }

    // =========================================================
    // RESUME UPLOAD
    // Maximum Size = 15 MB
    // =========================================================

    @PostMapping("/{id}/resume")
    public ResponseEntity<?> uploadResume(
            @PathVariable Long id,
            @RequestParam("resume") MultipartFile file)
            throws IOException {

        Application application =
                applicationRepository
                        .findById(id)
                        .orElse(null);

        if (application == null) {

            return ResponseEntity.notFound().build();
        }

        // -----------------------------------------------------
        // Empty File Check
        // -----------------------------------------------------

        if (file == null || file.isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Resume file is empty");
        }

        // -----------------------------------------------------
        // Maximum 15 MB
        // -----------------------------------------------------

        if (file.getSize() > 15 * 1024 * 1024) {

            return ResponseEntity.badRequest()
                    .body(
                        "Resume file size should be less than 15MB"
                    );
        }

        // -----------------------------------------------------
        // Original File Name
        // -----------------------------------------------------

        String originalFileName =
                file.getOriginalFilename();

        if (originalFileName == null ||
                originalFileName.trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Invalid resume file name");
        }

        // =====================================================
        // CREATE UPLOAD DIRECTORY
        // =====================================================

        Path uploadDirectory =
                Paths.get("uploads/resumes");

        Files.createDirectories(uploadDirectory);

        // =====================================================
        // CREATE UNIQUE FILE NAME
        // =====================================================

        String fileName =
                id + "_" + originalFileName;

        Path filePath =
                uploadDirectory.resolve(fileName);

        // =====================================================
        // SAVE FILE
        // =====================================================

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        // =====================================================
        // SAVE RESUME INFORMATION IN DATABASE
        // =====================================================

        application.setResumeFileName(
                originalFileName
        );

        application.setResumePath(
                filePath.toString()
        );

        return ResponseEntity.ok(
                applicationRepository.save(application)
        );
    }

    // =========================================================
    // VIEW / DOWNLOAD RESUME
    // =========================================================

    @GetMapping("/{id}/resume")
    public ResponseEntity<byte[]> getResume(
            @PathVariable Long id)
            throws IOException {

        Application application =
                applicationRepository
                        .findById(id)
                        .orElse(null);

        if (application == null ||
                application.getResumePath() == null) {

            return ResponseEntity.notFound().build();
        }

        Path filePath =
                Paths.get(
                        application.getResumePath()
                );

        if (!Files.exists(filePath)) {

            return ResponseEntity.notFound().build();
        }

        byte[] fileBytes =
                Files.readAllBytes(filePath);

        String fileName =
                application.getResumeFileName();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                fileName +
                                "\""
                )
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM
                )
                .body(fileBytes);
    }

    // =========================================================
    // DELETE APPLICATION
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApplication(
            @PathVariable Long id) {

        Application application =
                applicationRepository
                        .findById(id)
                        .orElse(null);

        if (application == null) {

            return ResponseEntity.notFound().build();
        }

        applicationRepository.delete(application);

        return ResponseEntity.ok(
                "Application Deleted Successfully"
        );
    }

    // =========================================================
    // BLACKLIST REQUEST
    // =========================================================

    public static class BlacklistRequest {

        private String reason;

        public String getReason() {

            return reason;
        }

        public void setReason(String reason) {

            this.reason = reason;
        }
    }
}