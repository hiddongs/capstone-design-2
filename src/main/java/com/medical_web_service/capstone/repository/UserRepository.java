package com.medical_web_service.capstone.repository;


import com.medical_web_service.capstone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findUserById(Long id);
    public User getUserById(Long userId);
    
    boolean existsByUsername(String username);  //  중복 체크
    boolean existsByPhone(String phone);        //  중복 체크
}
