package com.college.backend.controller;
import com.college.backend.entity.Event;
import com.college.backend.repository.EventRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import java.util.List;


@RestController
@CrossOrigin(origins = "*")
public class EventController {

    private final EventRepository eventRepository;
    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return eventRepository.findAllByOrderByIdDesc();
    }

    @GetMapping("/events/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventRepository.findById(id).orElse(null);
    }



    @PostMapping("/events")
    public Event addEvent(
            @RequestParam("title") String title,
            @RequestParam("eventDate") String eventDate,
            @RequestParam("category") String category,
            @RequestParam("venue") String venue,
            @RequestParam("summary") String summary,
            @RequestParam("file") MultipartFile file

    )throws Exception {

        String uploadDir = "uploads/";
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path path = Paths.get(uploadDir + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        Event event = new Event();
        event.setTitle(title);
        event.setEventDate(LocalDate.parse(eventDate));
        event.setCategory(category);
        event.setFilePath(fileName);
        event.setSummary(summary);
        event.setVenue(venue);

        return eventRepository.save(event);
    }
}
