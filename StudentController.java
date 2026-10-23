package com.example.crs.controller;

import com.example.crs.model.Student;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class StudentController {

    // In-memory "database"
    private static final Map<String, Student> studentDatabase = new ConcurrentHashMap<>();

    static {
        Student s = new Student();
        s.setStudentId("1001");
        s.setPassword("123456");
        s.setName("Test Student");
        s.setEmail("test@school.edu");
        studentDatabase.put("1001", s);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static ResponseEntity<Map<String, String>> msg(HttpStatus status, String text) {
        Map<String, String> body = new HashMap<>();
        body.put("message", text);
        return ResponseEntity.status(status).body(body);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String studentId = loginRequest.get("studentId");
        String password = loginRequest.get("password");

        if (isBlank(studentId) || isBlank(password)) {
            return msg(HttpStatus.BAD_REQUEST, "Student ID and Password cannot be empty.");
        }

        Student student = studentDatabase.get(studentId);

        if (student != null && student.getPassword().equals(password)) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", student.getStudentId());
            response.put("name", student.getName());
            return ResponseEntity.ok(response); // 200
        }
        return msg(HttpStatus.UNAUTHORIZED, "Invalid Student ID or Password"); // 401
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@RequestBody Student newStudent) {
        if (isBlank(newStudent.getStudentId()) || isBlank(newStudent.getPassword())) {
            return msg(HttpStatus.BAD_REQUEST, "Student ID and Password cannot be empty.");
        }

        // putIfAbsent is atomic, so two requests can't both register the same ID
        if (studentDatabase.putIfAbsent(newStudent.getStudentId(), newStudent) != null) {
            return msg(HttpStatus.CONFLICT, "Registration failed: Student ID already exists."); // 409
        }
        return msg(HttpStatus.CREATED, "Registration successful!"); // 201
    }
}
