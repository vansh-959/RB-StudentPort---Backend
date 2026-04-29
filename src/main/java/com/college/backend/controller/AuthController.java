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

    // 🔥 Temporary OTP store
    private final Map<String, String> otpStore = new HashMap<>();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🔐 1. SEND OTP
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String phone = request.get("phoneNumber");

        if (phone == null || !phone.matches("^[6-9]\\d{9}$")) {
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

    // 🔐 2. VERIFY OTP + SIGNUP
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String phone = request.get("phoneNumber");
        String otp = request.get("otp");

        String email = request.get("email");
        String password = request.get("password");
        String name = request.get("name");
        String branch = request.get("branch");

        if (phone == null || otp == null || !otpStore.containsKey(phone) || !otpStore.get(phone).equals(otp)) {
            response.put("success", false);
            response.put("message", "Invalid OTP");
            return ResponseEntity.badRequest().body(response);
        }

        if (email == null || password == null || name == null || branch == null) {
            response.put("success", false);
            response.put("message", "All fields are required");
            return ResponseEntity.badRequest().body(response);
        }

        if (userRepository.existsByEmail(email)) {
            response.put("success", false);
            response.put("message", "Email already exists");
            return ResponseEntity.badRequest().body(response);
        }

        User user = new User(email, password, name, phone, branch);
        userRepository.save(user);

        otpStore.remove(phone);

        response.put("success", true);
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }

    // 🔥 3. SIMPLE SIGNUP (FOR FRONTEND FIX)
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String email = request.get("email");
        String password = request.get("password");
        String name = request.get("name");
        String phoneNumber = request.get("phoneNumber");
        String branch = request.get("branch");

        if (email == null || password == null || name == null || phoneNumber == null || branch == null) {
            response.put("success", false);
            response.put("message", "All fields are required");
            return ResponseEntity.badRequest().body(response);
        }

        if (userRepository.existsByEmail(email)) {
            response.put("success", false);
            response.put("message", "Email already exists");
            return ResponseEntity.badRequest().body(response);
        }

        User user = new User(email, password, name, phoneNumber, branch);
        userRepository.save(user);

        response.put("success", true);
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }

    // 🔐 4. LOGIN
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String email = request.get("email");
        String password = request.get("password");

        if (email == null || password == null) {
            response.put("success", false);
            response.put("message", "Email and password are required");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
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
}