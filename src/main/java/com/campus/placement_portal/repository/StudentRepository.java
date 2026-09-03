package com.campus.placement_portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.campus.placement_portal.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Student findByEmail(String email);

    Student findByPhone(String phone);
}
