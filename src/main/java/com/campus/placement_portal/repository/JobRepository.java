
package com.campus.placement_portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.placement_portal.entity.job;

public interface JobRepository extends JpaRepository<job, Long> {

    // Get all jobs posted by a specific company
    List<job> findByCompanyId(Long companyId);
}

