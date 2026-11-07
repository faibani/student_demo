package com.example.crs.controller;
import com.example.crs.model.Student; // import Student 

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = "http://localhost:5173") // Stay consistent with CourseController for Vue
@RestController
@RequestMapping("/api")
public class StudentController {

    // Task 1: Simulate "in-memory" Student "Database"
    private static final Map<String, Student> studentDatabase = new ConcurrentHashMap<>();

    // generate a test account 
    static {
        Student defaultStudent = new Student();
        defaultStudent.setStudentId("1001");
        defaultStudent.setPassword("123456");
        defaultStudent.setName("Test Student");
        defaultStudent.setEmail("test@school.edu");
        studentDatabase.put("1001", defaultStudent);
    }

    // ==========================================
    // Please finish the codes for Step2/3, where you can use AI
    // ==========================================
    
    @PostMapping("/login")
    //public ResponseEntity<?> login(...) { ... }   
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String studentId = loginRequest.get("studentId");
        String password = loginRequest.get("password");

        // 1. verify the input student ID and password are not empty 
        if (studentId == null || studentId.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            
            Map<String, String> error = new HashMap<>();
            error.put("message", "Student ID and Password cannot be empty.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // 2. check the student existence 
        Student student = studentDatabase.get(studentId);

        // 3. validate the login info 
        if (student != null && student.getPassword().equals(password)) {
            // success, assemble the data back to  Vue
            Map<String, Object> response = new HashMap<>();
            response.put("id", student.getStudentId());
            response.put("name", student.getName());
            
            // send 200 OK along with student info
            return ResponseEntity.ok(response);
        } else {
            // login fail
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid Student ID or Password");
            
            // send 401 Unauthorized 
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }


     @PostMapping("/register")
     public ResponseEntity<?> registerStudent(@RequestBody Student newStudent) {
         // validate data, make sure not null
         if (newStudent.getStudentId() == null || ((String) newStudent.getStudentId()).trim().isEmpty() ||
             newStudent.getPassword() == null || ((String) newStudent.getPassword()).trim().isEmpty()) {
             Map<String, String> errorResponse = new HashMap<>();
             errorResponse.put("message", "Student ID and Password cannot be empty.");
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
         }

         // check studentId has been created or not
         if (studentDatabase.containsKey(newStudent.getStudentId())) {
             Map<String, String> errorResponse = new HashMap<>();
             // align with  Vue frontend  
             errorResponse.put("message", "Registration failed: Student ID already exists.");
             
             // return 409 Conflict status code
             return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
         }

         // if it is unique StudentID, "insert" into the simulated database
         studentDatabase.put((String) newStudent.getStudentId(), newStudent);

         // successfully registered
         Map<String, String> successResponse = new HashMap<>();
         successResponse.put("message", "Registration successful!");
         
         // return 201 Created status code
         return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
     }


@PostMapping("/remove")
public ResponseEntity<?> removeStudent(@RequestBody Map<String, String> req) {
    String id = req.get("studentId");
    String password = req.get("password");
    Map<String, String> result = new HashMap<>();

    Student student = studentDatabase.get(id);

    if (student == null || !student.getPassword().equals(password)) {
        result.put("message", "Student ID or password is wrong.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    studentDatabase.remove(id);
    result.put("message", "Removed: " + student.getStudentId() + ", " + student.getName() + ", " + student.getEmail());
    return ResponseEntity.ok(result);

}}
