package com.example.evernorth2.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @Column(name = "member_id") 
    private String member_id;  // Updated to String for formatted member_id (e.g., "EVE-YYYYMMDD-XXXXX")

    private String full_name;
    private String email_id;
    private String contact_no;
    private Date date_of_birth; // Changed from Byte age to Date date_of_birth
    @CreationTimestamp
    private Timestamp created_at;
    @UpdateTimestamp
    private Timestamp updated_at;
    private String otp; // Temporary field for OTP storage

    // Getter and Setter for OTP
    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    // Getters and setters
    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public Date getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(Date date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }

    private Instant otpTimestamp;
    
    public Instant getOtpTimestamp() {
        return otpTimestamp;
    }

    // Setter for otpTimestamp
    public void setOtpTimestamp(Instant otpTimestamp) {
        this.otpTimestamp = otpTimestamp;
    }
}
