package com.college.backend.controller;

import com.college.backend.entity.Note;
import com.college.backend.repository.NoteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    private final NoteRepository noteRepository;

    public NoteController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    // 1. Saare Notes dekhan layi
    @GetMapping
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    // 2. Kise ik Note di detail dekhan layi (ID de hisab naal)
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        return noteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Naye Notes upload karan layi
    @PostMapping("/upload")
    public Note uploadNote(
            @RequestParam("stream") String stream,
            @RequestParam("branch") String branch,
            @RequestParam("semester") int semester,
            @RequestParam("year") int year,
            @RequestParam("subjectName") String subjectName,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file) throws Exception {

        String uploadDir = System.getProperty("user.dir") + "/uploads/notes/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), filePath);

        Note note = new Note();
        note.setStream(stream);
        note.setBranch(branch);
        note.setSemester(semester);
        note.setYear(year);
        note.setSubjectName(subjectName);
        note.setDescription(description);
        note.setFilePath(fileName);

        return noteRepository.save(note);
    }
}