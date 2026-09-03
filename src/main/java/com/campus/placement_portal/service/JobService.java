package com.campus.placement_portal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.campus.placement_portal.entity.job;
import com.campus.placement_portal.repository.JobRepository;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // ==============================
    // Create new job
    // ==============================

    public job createJob(job job) {
        return jobRepository.save(job);
    }

    // ==============================
    // Get all jobs
    // ==============================

    public List<job> getAllJobs() {
        return jobRepository.findAll();
    }

    // ==============================
    // Get job by ID
    // ==============================

    public job getJobById(Long id) {
        return jobRepository
                .findById(id)
                .orElse(null);
    }

    // ==============================
    // Get company jobs
    // ==============================

    public List<job> getJobsByCompanyId(Long companyId) {
        return jobRepository.findByCompanyId(companyId);
    }

    // ==============================
    // Existing update
    // ==============================

    public job updateJob(Long id, job updatedJob) {

        job existingJob =
                jobRepository
                        .findById(id)
                        .orElse(null);

        if (existingJob != null) {

            copyJobData(existingJob, updatedJob);

            return jobRepository.save(existingJob);
        }

        return null;
    }

    // ==============================
    // Secure company update
    // ==============================

    public job updateJobForCompany(
            Long id,
            Long companyId,
            job updatedJob) {

        job existingJob =
                jobRepository
                        .findById(id)
                        .orElse(null);

        if (existingJob == null) {
            return null;
        }

        if (existingJob.getCompanyId() == null ||
                !existingJob.getCompanyId()
                        .equals(companyId)) {

            return null;
        }

        copyJobData(existingJob, updatedJob);

        // Company ID NEVER changes
        existingJob.setCompanyId(companyId);

        return jobRepository.save(existingJob);
    }

    // ==============================
    // Delete job
    // ==============================

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    // ==============================
    // Secure company delete
    // ==============================

    public boolean deleteJobForCompany(
            Long id,
            Long companyId) {

        job existingJob =
                jobRepository
                        .findById(id)
                        .orElse(null);

        if (existingJob == null) {
            return false;
        }

        if (existingJob.getCompanyId() == null ||
                !existingJob.getCompanyId()
                        .equals(companyId)) {

            return false;
        }

        jobRepository.delete(existingJob);

        return true;
    }

    // ==============================
    // Copy editable job fields
    // ==============================

    private void copyJobData(
            job existingJob,
            job updatedJob) {

        existingJob.setJobTitle(
                updatedJob.getJobTitle()
        );

        existingJob.setCompanyName(
                updatedJob.getCompanyName()
        );

        existingJob.setLocation(
                updatedJob.getLocation()
        );

        existingJob.setJobType(
                updatedJob.getJobType()
        );

        existingJob.setExperience(
                updatedJob.getExperience()
        );

        existingJob.setSalary(
                updatedJob.getSalary()
        );

        existingJob.setSkills(
                updatedJob.getSkills()
        );

        existingJob.setDescription(
                updatedJob.getDescription()
        );

        existingJob.setLastDate(
                updatedJob.getLastDate()
        );

        existingJob.setVacancies(
                updatedJob.getVacancies()
        );
    }
}

