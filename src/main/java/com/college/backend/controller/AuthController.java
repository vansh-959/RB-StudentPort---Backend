package com.college.backend.controller;

import com.college.backend.entity.User;
import com.college.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;

    // 🔥 Temporary OTP store (Original Logic)
    private final Map<String, String> otpStore = new HashMap<>();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🔐 1. SEND OTP (Original)
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String phone = request.get("phoneNumber");

        if (phone == null || !phone.matches("^[5-9]\\d{9}$")) {
            response.put("success", false);
            response.put("message", "Invalid phone number");
            return ResponseEntity.badRequest().body(response);
        }

        if (userRepository.existsByPhoneNumber(phone)) {
            response.put("success", false);
            response.put("message", "Phone number already registered");
            return ResponseEntity.badRequest().body(response);
        }

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        otpStore.put(phone, otp);
        System.out.println("OTP for " + phone + " = " + otp);

        response.put("success", true);
        response.put("message", "OTP sent successfully");
        return ResponseEntity.ok(response);
    }

    // 🔐 2. VERIFY OTP + SIGNUP (Original)
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String phone = request.get("phoneNumber");
        String otp = request.get("otp");

        if (phone == null || otp == null || !otpStore.containsKey(phone) || !otpStore.get(phone).equals(otp)) {
            response.put("success", false);
            response.put("message", "Invalid OTP");
            return ResponseEntity.badRequest().body(response);
        }

        User user = new User(
            request.get("email"), 
            request.get("password"), 
            request.get("name"), 
            phone, 
            request.get("branch")
        );
        userRepository.save(user);
        otpStore.remove(phone);

        response.put("success", true);
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }

    // 🔥 3. SIMPLE SIGNUP (Original)
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String email = request.get("email");

        if (!phoneNumber.matches("^[5-9]\\d{9}$")) {
            response.put("success", false);
            response.put("message", "Invalid phone number");
            return ResponseEntity.badRequest().body(response);
        }

        if (userRepository.existsByEmail(email)) {
            response.put("success", false);
            response.put("message", "Email already exists");
            return ResponseEntity.badRequest().body(response);
        }

        User user = new User(
            email, 
            request.get("password"), 
            request.get("name"), 
            request.get("phoneNumber"), 
            request.get("branch")
        );
        userRepository.save(user);

        response.put("success", true);
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }

    // 🔐 4. LOGIN (Original)
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> userOpt = userRepository.findByEmail(request.get("email"));

        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(request.get("password"))) {
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userOpt.get();
        response.put("success", true);
        response.put("message", "Login successful");
        response.put("user", Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "branch", user.getBranch()
        ));
        return ResponseEntity.ok(response);
    }

    // ⭐ 5. NEW: GET ALL USERS (Admin Panel Layi)
    // Is naal "Unable to load users" wala error fix ho jayega
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<User> allUsers = userRepository.findAll();
            
            // Search filter logic
            List<User> filtered = allUsers.stream()
                .filter(u -> (name == null || u.getName().toLowerCase().contains(name.toLowerCase())))
                .filter(u -> (department == null || u.getBranch().toLowerCase().contains(department.toLowerCase())))
                .toList();

            int start = Math.min(page * size, filtered.size());
            int end = Math.min(start + size, filtered.size());

            response.put("success", true);
            response.put("users", filtered.subList(start, end));
            response.put("page", page);
            response.put("totalPages", (int) Math.ceil((double) filtered.size() / size));
            response.put("totalElements", filtered.size());
            response.put("hasPrevious", page > 0);
            response.put("hasNext", end < filtered.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}