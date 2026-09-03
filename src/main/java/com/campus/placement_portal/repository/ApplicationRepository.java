package com.campus.placement_portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.placement_portal.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByCompanyId(Long companyId);

    List<Application> findByJobIdIn(List<Long> jobIds);

    List<Application> findByStudentId(Long studentId);

    List<Application> findByStudentIdAndCompanyIdAndBlacklistedTrue(
            Long studentId,
            Long companyId
    );
}