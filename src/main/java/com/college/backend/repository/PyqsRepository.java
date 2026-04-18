package com.college.backend.repository;

import com.college.backend.entity.Pyqs;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PyqsRepository  extends JpaRepository<Pyqs, Long> {
    List<Pyqs> findAllByOrderByIdDesc();

}