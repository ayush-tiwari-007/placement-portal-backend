
package com.campus.placement_portal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.campus.placement_portal.entity.Application;
import com.campus.placement_portal.repository.ApplicationRepository;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    // =========================================================
    // Create / Apply for Job
    // =========================================================

    public Application applyJob(Application application) {

        if (application.getStatus() == null ||
                application.getStatus().trim().isEmpty()) {

            application.setStatus("Applied");
        }

        if (application.getBlacklisted() == null) {
            application.setBlacklisted(false);
        }

        return applicationRepository.save(application);
    }

    // =========================================================
    // Get all applications
    // =========================================================

    public List<Application> getAllApplications() {

        return applicationRepository.findAll();
    }

    // =========================================================
    // Get application by ID
    // =========================================================

    public Application getApplicationById(Long id) {

        return applicationRepository
                .findById(id)
                .orElse(null);
    }

    // =========================================================
    // Get applications for company's jobs
    // =========================================================

    public List<Application> getApplicationsByCompanyId(
            Long companyId,
            List<Long> jobIds) {

        if (jobIds == null || jobIds.isEmpty()) {
            return List.of();
        }

        return applicationRepository.findByJobIdIn(jobIds);
    }

    // =========================================================
    // Update application status
    // =========================================================

    public Application updateApplication(
            Long id,
            Application updatedApplication) {

        Application existingApplication =
                applicationRepository
                        .findById(id)
                        .orElse(null);

        if (existingApplication == null) {
            return null;
        }

        existingApplication.setStudentId(
                updatedApplication.getStudentId()
        );

        existingApplication.setJobId(
                updatedApplication.getJobId()
        );

        existingApplication.setStatus(
                updatedApplication.getStatus()
        );

        existingApplication.setAppliedDate(
                updatedApplication.getAppliedDate()
        );

        return applicationRepository.save(existingApplication);
    }

    // =========================================================
    // Select Candidate
    // =========================================================

    public Application selectCandidate(Long id) {

        Application application =
                applicationRepository
                        .findById(id)
                        .orElse(null);

        if (application == null) {
            return null;
        }

        application.setStatus("Selected");

        return applicationRepository.save(application);
    }

    // =========================================================
    // Schedule Interview
    // =========================================================

    public Application scheduleInterview(
            Long id,
            Application interviewData) {

        Application application =
                applicationRepository
                        .findById(id)
                        .orElse(null);

        if (application == null) {
            return null;
        }

        application.setInterviewDate(
                interviewData.getInterviewDate()
        );

        application.setInterviewTime(
                interviewData.getInterviewTime()
        );

        application.setInterviewMode(
                interviewData.getInterviewMode()
        );

        application.setInterviewRound(
                interviewData.getInterviewRound()
        );

        application.setMeetingLink(
                interviewData.getMeetingLink()
        );

        application.setInterviewVenue(
                interviewData.getInterviewVenue()
        );

        application.setInterviewInstructions(
                interviewData.getInterviewInstructions()
        );

        application.setStatus(
                "Interview Scheduled"
        );

        return applicationRepository.save(application);
    }

    // =========================================================
    // Blacklist Student
    // =========================================================

    public Application blacklistStudent(
            Long id,
            String reason) {

        Application application =
                applicationRepository
                        .findById(id)
                        .orElse(null);

        if (application == null) {
            return null;
        }

        application.setBlacklisted(true);

        application.setBlacklistReason(reason);

        application.setStatus("Blacklisted");

        return applicationRepository.save(application);
    }

    // =========================================================
    // Check whether student is blacklisted by company
    // =========================================================

    public boolean isStudentBlacklisted(
            Long studentId,
            Long companyId) {

        return !applicationRepository
                .findByStudentIdAndCompanyIdAndBlacklistedTrue(
                        studentId,
                        companyId
                )
                .isEmpty();
    }

    // =========================================================
    // Delete application
    // =========================================================

    public void deleteApplication(Long id) {

        applicationRepository.deleteById(id);
    }
}
