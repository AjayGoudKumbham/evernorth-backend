package com.example.evernorth2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.evernorth2.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u.member_id FROM User u WHERE u.email_id = :emailId")
    String findMemberIdByEmail(@Param("emailId") String emailId);

    @Query("SELECT u FROM User u WHERE u.member_id = :memberId")
    Optional<User> findByMemberId(@Param("memberId") String memberId);
}
