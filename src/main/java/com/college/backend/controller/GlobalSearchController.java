package com.college.backend.controller;

import com.college.backend.repository.EventRepository;
import com.college.backend.repository.NoticeRepository;
import com.college.backend.repository.PyqsRepository;
import com.college.backend.repository.NoteRepository; // 1. NoteRepository import karo
import com.college.backend.entity.Event;
import com.college.backend.entity.Note;
import com.college.backend.entity.Notice;
import com.college.backend.entity.Pyqs;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    // Controller de andar @GetMapping("/all") de vich:
@GetMapping("/all")
public Map<String, Object> searchAll(@RequestParam String query) {
    Map<String, Object> results = new HashMap<>();

    List<Set<String>> tokenGroups = buildTokenGroups(query);

    results.put("events", filterEvents(tokenGroups));
    results.put("notices", filterNotices(tokenGroups));
    results.put("pyqs", filterPyqs(tokenGroups));
    results.put("notes", filterNotes(tokenGroups));

    return results;
}

private List<Event> filterEvents(List<Set<String>> tokenGroups) {
    return eventRepository.findAllByOrderByIdDesc().stream()
            .filter(event -> matchesAllTokens(tokenGroups, event.getTitle(), event.getCategory(), event.getVenue(), event.getSummary()))
            .collect(Collectors.toList());
}

private List<Notice> filterNotices(List<Set<String>> tokenGroups) {
    return noticeRepository.findAllByOrderByIdDesc().stream()
            .filter(notice -> matchesAllTokens(tokenGroups, notice.getTitle(), notice.getCategory()))
            .collect(Collectors.toList());
}

private List<Pyqs> filterPyqs(List<Set<String>> tokenGroups) {
    return pyqsRepository.findAllByOrderByIdDesc().stream()
            .filter(pyq -> matchesAllTokens(tokenGroups,
                    pyq.getSubject(),
                    pyq.getBranch(),
                    pyq.getStream(),
                    pyq.getYear() == null ? null : pyq.getYear().toString(),
                    pyq.getSemester() == null ? null : pyq.getSemester().toString()))
            .collect(Collectors.toList());
}

private List<Note> filterNotes(List<Set<String>> tokenGroups) {
    return noteRepository.findAll().stream()
            .filter(note -> matchesAllTokens(tokenGroups,
                    note.getSubjectName(),
                    note.getDescription(),
                    note.getBranch(),
                    note.getStream(),
                    String.valueOf(note.getYear()),
                    String.valueOf(note.getSemester())))
            .collect(Collectors.toList());
}

private List<Set<String>> buildTokenGroups(String query) {
    List<Set<String>> tokenGroups = new ArrayList<>();

    for (String rawToken : query.toLowerCase().trim().split("\\s+")) {
        String token = normalizeToken(rawToken);
        if (token.isBlank()) {
            continue;
        }

        Set<String> group = new LinkedHashSet<>();
        group.add(token);

        switch (token) {
            case "pyq", "pyqs" -> {
                group.add("question");
                group.add("paper");
                group.add("previous");
            }
            case "question", "paper", "papers" -> {
                group.add("pyq");
                group.add("pyqs");
            }
            case "note", "notes", "material", "materials" -> {
                group.add("note");
                group.add("notes");
                group.add("material");
            }
            case "program" -> group.add("programm");
            case "programm" -> group.add("program");
            default -> {
            }
        }

        tokenGroups.add(group);
    }

    return tokenGroups;
}

private boolean matchesAllTokens(List<Set<String>> tokenGroups, String... fields) {
    String haystack = Arrays.stream(fields)
            .filter(field -> field != null && !field.isBlank())
            .map(this::normalizeText)
            .collect(Collectors.joining(" "));

    return tokenGroups.stream().anyMatch(group -> 
    group.stream().anyMatch(haystack::contains)
);
}

private String normalizeText(String text) {
    return Arrays.stream(text.toLowerCase().replaceAll("[^a-z0-9]+", " ").trim().split("\\s+"))
            .filter(token -> !token.isBlank())
            .map(this::normalizeToken)
            .collect(Collectors.joining(" "));
}

private String normalizeToken(String token) {
    String cleaned = token.toLowerCase().replaceAll("[^a-z0-9]", "");
    if (cleaned.length() > 5 && cleaned.endsWith("ing")) {
        cleaned = cleaned.substring(0, cleaned.length() - 3);
    } else if (cleaned.length() > 4 && cleaned.endsWith("es")) {
        cleaned = cleaned.substring(0, cleaned.length() - 2);
    } else if (cleaned.length() > 3 && cleaned.endsWith("s")) {
        cleaned = cleaned.substring(0, cleaned.length() - 1);
    }
    return cleaned;
}
}