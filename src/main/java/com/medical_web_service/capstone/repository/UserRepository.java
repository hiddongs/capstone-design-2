package com.medical_web_service.capstone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.medical_web_service.capstone.entity.Role;
import com.medical_web_service.capstone.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findUserById(Long id);
    User getUserById(Long userId);

    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);

    List<User> findByRole(Role role);
    List<User> findByRoleAndDepartment(Role role, String department);

    // ⭐ LazyInitializationException 해결용 Fetch Join 추가
    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.diseaseHistory " +
            "LEFT JOIN FETCH u.searchingDiseaseHistories " +
            "WHERE u.id = :id")
    User findUserWithHistories(@Param("id") Long id);
}
