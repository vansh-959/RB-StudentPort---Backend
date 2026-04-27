package com.college.backend.controller;

import com.college.backend.entity.Notice;
import com.college.backend.repository.NoticeRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import java.util.List;

//handles http requests

@RestController  //handle APIs and return java objects as JSON
@RequestMapping("/api/notices")
@CrossOrigin(origins = "*")  //frontend and backend runs on different ports so it allows requests from any origin
public class NoticeController {

    private final NoticeRepository noticeRepository;
    public NoticeController(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }
//spring Boot has created object automatically and it is called dependency injection
//controller needs something(dependency) to work
//injection means you are not creating object using new , spring is created it for you


    @GetMapping//helps to fetch data from database and get the data
    public List<Notice> getAllNotices() {
        return noticeRepository.findAllByOrderByIdDesc();
    }


    @GetMapping("/{id}")
    public Notice getNoticeById(@PathVariable Long id) {
        return noticeRepository.findById(id).orElse(null);
    }

    @PostMapping //handles data uploading by running notice method
    public Notice addNotice(
            @RequestParam("title") String title,
            @RequestParam("publicationDate") String publicationDate,
            @RequestParam("category") String category,
            @RequestParam("file") MultipartFile file
    )throws Exception {

        String uploadDir = "uploads/"; //creates upload folder if not created
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename(); //System.currentTimeMillis() avoid duplicate file names.

        Path path = Paths.get(uploadDir + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setPublicationDate(LocalDate.parse(publicationDate));
        notice.setCategory(category);
        notice.setPdfPath(fileName);

        return noticeRepository.save(notice);  //saves into database
    }

}
