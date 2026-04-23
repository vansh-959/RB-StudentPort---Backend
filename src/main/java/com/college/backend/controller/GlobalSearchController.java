package com.college.backend.controller;

import com.college.backend.repository.EventRepository;
import com.college.backend.repository.NoticeRepository;
import com.college.backend.repository.PyqsRepository;
import com.college.backend.repository.NoteRepository; // 1. NoteRepository import karo
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class GlobalSearchController {

    private final EventRepository eventRepository;
    private final NoticeRepository noticeRepository;
    private final PyqsRepository pyqsRepository;
    private final NoteRepository noteRepository; // 2. NoteRepository variable add karo

    public GlobalSearchController(EventRepository eventRepository, 
                                  NoticeRepository noticeRepository, 
                                  PyqsRepository pyqsRepository,
                                  NoteRepository noteRepository) { // 3. Constructor vich add karo
        this.eventRepository = eventRepository;
        this.noticeRepository = noticeRepository;
        this.pyqsRepository = pyqsRepository;
        this.noteRepository = noteRepository;
    }

    @GetMapping("/all")
    public Map<String, Object> searchAll(@RequestParam String query) {
        Map<String, Object> results = new HashMap<>();
        
        results.put("events", eventRepository.findByTitleContainingIgnoreCaseOrderByIdDesc(query));
        results.put("notices", noticeRepository.findByTitleContainingIgnoreCase(query));
        results.put("pyqs", pyqsRepository.findBySubjectContainingIgnoreCaseOrBranchContainingIgnoreCaseOrFileNameContainingIgnoreCase(query, query, query));
        
        // 4. Notes search results add karo
        results.put("notes", noteRepository.findBySubjectNameContainingIgnoreCase(query));
        
        return results;
    }
}