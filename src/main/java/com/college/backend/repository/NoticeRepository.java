package com.college.backend.repository;

import com.college.backend.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByOrderByIdDesc(); // Eh line add karo
    List<Notice> findByTitleContainingIgnoreCase(String title);
}