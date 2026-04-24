package com.college.backend.repository;

import com.college.backend.entity.Pyqs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PyqsRepository extends JpaRepository<Pyqs, Long> {
    List<Pyqs> findAllByOrderByIdDesc(); // Eh line add karo
    List<Pyqs> findBySubjectContainingIgnoreCase(String query); // Eh line vi add karo (Controller mang reha hai)
    
    @Query("SELECT p FROM Pyqs p WHERE LOWER(p.subject) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(p.branch) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Pyqs> searchGlobal(@Param("q") String query);
}