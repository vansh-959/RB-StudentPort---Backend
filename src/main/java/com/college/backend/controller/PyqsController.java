package com.college.backend.controller;

import com.college.backend.entity.Pyqs;
import com.college.backend.repository.PyqsRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@CrossOrigin(origins = "*") // Eh zaruri hai taaki frontend backend naal gal kar sake
public class PyqsController {

    private final PyqsRepository pyqsRepository;

    public PyqsController(PyqsRepository pyqsRepository) {
        this.pyqsRepository = pyqsRepository;
    }

    // 1. Saare PYQs get karo
    @GetMapping("/pyqs")
    public List<Pyqs> getAllPyqs() {
        return pyqsRepository.findAllByOrderByIdDesc();
    }

    // 2. 🔍 SEARCH ENDPOINT (Nawa Code)
    @GetMapping("/pyqs/search")
    public List<Pyqs> searchPyqs(@RequestParam("query") String query) {
        return pyqsRepository.findBySubjectContainingIgnoreCase(query);
    }

    @GetMapping("/pyqs/{id}")
    public Pyqs getPyqsById(@PathVariable Long id) {
        return pyqsRepository.findById(id).orElse(null);
    }

    // 3. PYQs Add/Upload karo
    @PostMapping("/pyqs")
    public List<Pyqs> addPyqs(
            @RequestParam("stream") String stream,
            @RequestParam("branch") String branch,
            @RequestParam("semester") Integer semester,
            @RequestParam("year") Integer year,
            @RequestParam("subject") String subject,
            @RequestParam("pyqFiles") MultipartFile[] pyqsFiles
    ) throws Exception {

        String uploadDir = System.getProperty("user.dir") + "/uploads/pyqs/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<Pyqs> savedFiles = new ArrayList<>();

        for (MultipartFile file : pyqsFiles) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath);

            Pyqs pyqs = new Pyqs();
            pyqs.setStream(stream);
            pyqs.setBranch(branch);
            pyqs.setSemester(semester);
            pyqs.setFileName(fileName);
            pyqs.setSubject(subject);
            pyqs.setYear(year);

            savedFiles.add(pyqsRepository.save(pyqs));
        }
        return savedFiles;
    }
}