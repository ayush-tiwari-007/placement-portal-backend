
package com.campus.placement_portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.placement_portal.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Company findByEmail(String email);

}
