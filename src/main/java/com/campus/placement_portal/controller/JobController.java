
package com.campus.placement_portal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.campus.placement_portal.entity.job;
import com.campus.placement_portal.service.JobService;

@RestController
@RequestMapping("/jobs")
@CrossOrigin(origins = "http://localhost:3000")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // ==============================
    // Get all jobs
    // ==============================

    @GetMapping
    public List<job> getAllJobs() {

        return jobService.getAllJobs();
    }

    // ==============================
    // Create new job
    // ==============================

    @PostMapping
    public job addJob(
            @RequestBody job job) {

        // Company ID is mandatory
        if (job.getCompanyId() == null) {

            throw new RuntimeException(
                    "Company ID is required"
            );
        }

        // Save job with company ID
        return jobService.createJob(job);
    }

    // ==============================
    // Get job by ID
    // ==============================

    @GetMapping("/{id}")
    public job getJobById(
            @PathVariable Long id) {

        return jobService.getJobById(id);
    }

    // ==============================
    // Get jobs of a company
    // ==============================

    @GetMapping("/company/{companyId}")
    public List<job> getJobsByCompanyId(
            @PathVariable Long companyId) {

        return jobService.getJobsByCompanyId(
                companyId
        );
    }

    // ==============================
    // Existing update
    // ==============================

    @PutMapping("/{id}")
    public job updateJob(
            @PathVariable Long id,
            @RequestBody job job) {

        return jobService.updateJob(
                id,
                job
        );
    }

    // ==============================
    // Secure company update
    // ==============================

    @PutMapping("/{id}/company/{companyId}")
    public job updateJobForCompany(
            @PathVariable Long id,
            @PathVariable Long companyId,
            @RequestBody job job) {

        return jobService.updateJobForCompany(
                id,
                companyId,
                job
        );
    }

    // ==============================
    // Existing delete
    // ==============================

    @DeleteMapping("/{id}")
    public String deleteJob(
            @PathVariable Long id) {

        jobService.deleteJob(id);

        return "Job Deleted Successfully";
    }

    // ==============================
    // Secure company delete
    // ==============================

    @DeleteMapping("/{id}/company/{companyId}")
    public String deleteJobForCompany(
            @PathVariable Long id,
            @PathVariable Long companyId) {

        boolean deleted =
                jobService.deleteJobForCompany(
                        id,
                        companyId
                );

        if (!deleted) {

            return "You are not authorized to delete this job";
        }

        return "Job Deleted Successfully";
    }
}
