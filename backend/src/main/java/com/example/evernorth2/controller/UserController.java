package com.example.evernorth2.controller;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.evernorth2.model.User;
import com.example.evernorth2.repository.UserRepository;
import com.example.evernorth2.service.EmailService;
import com.example.evernorth2.service.UserService;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    // Endpoint to create user
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            String memberId = generateMemberId();
            user.setMember_id(memberId);
            User createdUser = userService.createUser(user);

            String subject = "Welcome to Evernorth";
            String message = "Dear " + createdUser.getFull_name() + ",\n\n" +
                             "Welcome to Evernorth! Your member ID is: " + createdUser.getMember_id() +
                             ".\n\nThank you for joining us.\n\nBest regards,\nEvernorth Team";

            emailService.sendEmail(createdUser.getEmail_id(), subject, message);

            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private String generateMemberId() {
        String memberId = "";
        boolean isUnique = false;

        while (!isUnique) {
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));
            Random random = new Random();
            int randomNum = 1000 + random.nextInt(9000);

            memberId = currentDate + String.format("%04d", randomNum);
            Optional<User> existingUser = userRepository.findByMemberId(memberId);

            if (existingUser.isEmpty()) {
                isUnique = true;
            }
        }

        return memberId;
    }

    private String generateAndSendOtp(User existingUser) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        existingUser.setOtp(otp);
        existingUser.setOtpTimestamp(Instant.now());
        userRepository.save(existingUser);

        String message = "Dear " + existingUser.getFull_name() + ",\n\nYour OTP is: " + otp +
                ".\n\nPlease use this OTP to verify your details.\n\nBest regards,\nYour Team";
        emailService.sendEmail(existingUser.getEmail_id(), "Your OTP Verification", message);

        return "OTP sent successfully to " + existingUser.getEmail_id();
    }

    // DTO classes for the API endpoints
    public static class GenerateOtpRequest {
        private String memberId;
        private String dateOfBirth;
        private String contactNo;

        // Getters and setters
        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public String getContactNo() {
            return contactNo;
        }

        public void setContactNo(String contactNo) {
            this.contactNo = contactNo;
        }
    }

    public static class ValidateOtpRequest {
        private String memberId;
        private String contactNo;
        private String otp;

        // Getters and setters
        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        public String getContactNo() {
            return contactNo;
        }

        public void setContactNo(String contactNo) {
            this.contactNo = contactNo;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }
    }

    // Endpoint to generate OTP
    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody GenerateOtpRequest request) {
        try {
            Optional<User> userOptional = userRepository.findByMemberId(request.getMemberId());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error: User with Member ID " + request.getMemberId() + " not found.");
            }

            User user = userOptional.get();

            if (!user.getDate_of_birth().toString().equals(request.getDateOfBirth())) {
                return ResponseEntity.badRequest().body("Error: Date of birth does not match.");
            }

            if (!user.getContact_no().equals(request.getContactNo())) {
                return ResponseEntity.badRequest().body("Error: Contact number does not match.");
            }

            return ResponseEntity.ok(generateAndSendOtp(user));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    // Endpoint to validate OTP
    @PostMapping("/validate-otp")
    public ResponseEntity<String> validateOtp(@RequestBody ValidateOtpRequest request) {
        try {
            Optional<User> userOptional = userRepository.findByMemberId(request.getMemberId());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error: User with Member ID " + request.getMemberId() + " not found.");
            }

            User user = userOptional.get();

            if (!user.getOtp().equals(request.getOtp())) {
                return ResponseEntity.badRequest().body("Error: Invalid OTP.");
            }

            Duration duration = Duration.between(user.getOtpTimestamp(), Instant.now());
            if (duration.getSeconds() > 30) {
                return ResponseEntity.badRequest().body("Error: OTP expired.");
            }

            return ResponseEntity.ok("Validation successful!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }
}
