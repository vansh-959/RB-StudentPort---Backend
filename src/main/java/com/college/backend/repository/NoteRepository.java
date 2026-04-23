package com.college.backend.repository;

import com.college.backend.entity.Note; // Note entity nu import kita
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    
    // Search bar layi (Subject name naal search karan layi)
    List<Note> findBySubjectNameContainingIgnoreCase(String query);

    // Filter layi (Branch te Semester de hisab naal)
    List<Note> findByBranchAndSemester(String branch, int semester);
}