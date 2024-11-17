package com.example.evernorth2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.evernorth2.model.User;
import com.example.evernorth2.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);  // Save the user
    }
    public String findMemberIdByEmail(String email) {
        return userRepository.findMemberIdByEmail(email);
    }

}

