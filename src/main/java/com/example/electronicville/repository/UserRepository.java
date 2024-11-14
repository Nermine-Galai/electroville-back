package com.example.electronicville.repository;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.example.electronicville.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer>{
    List<User> findByStatus(String status);
    User findByEmail(String email);
}
