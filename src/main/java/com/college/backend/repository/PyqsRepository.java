package com.college.backend.repository;

import com.college.backend.entity.Pyqs;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PyqsRepository extends JpaRepository<Pyqs, Long> {
    
    // Saare PYQs dikhaun layi
    List<Pyqs> findAllByOrderByIdDesc();

    // 🔍 SEARCH LOGIC: Subject de base te labhan layi
    List<Pyqs> findBySubjectContainingIgnoreCase(String subject);
}