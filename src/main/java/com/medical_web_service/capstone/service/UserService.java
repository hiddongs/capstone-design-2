package com.medical_web_service.capstone.service;


import com.medical_web_service.capstone.dto.AuthDto;
import com.medical_web_service.capstone.entity.User;
import com.medical_web_service.capstone.repository.UserRepository;
import com.medical_web_service.capstone.service.UserDetailsImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final PasswordEncoder encoder;


    @Transactional
    public void registerUser(AuthDto.SignupDto signupDto) {
        
        if (userRepository.existsByUsername(signupDto.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        //  전화번호 중복 체크
        if (userRepository.existsByPhone(signupDto.getPhone())) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }

        User user = User.registerUser(signupDto);
        userRepository.save(user);
       
    }

    @Transactional
    public void updateUser(Long userId, AuthDto.UpdateDto updateDto, String encodedPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        // 아이디 중복 확인
       
        if (!user.getUsername().equals(updateDto.getUsername()) && isUsernameTaken(updateDto.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }


        // UpdateDto로부터 업데이트할 사용자 정보 가져오기
        String newUsername = updateDto.getUsername();
        String newPassword = encodedPassword;
        String newName = updateDto.getName();
        String newPhone = updateDto.getPhone();
        user.setUsername(newUsername);
        user.setPassword(newPassword);
        user.setName(newName);

        user.setPhone(newPhone);
        userRepository.save(user);
    }
    @Transactional(readOnly = true)
    public User getUserWithHistories(Long userId) {
        return userRepository.findUserWithHistories(userId);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // 사용자 ID로 사용자를 조회하는 메서드
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
    @Transactional
    public User getUserById(Long userId) {
        // UserRepository를 사용하여 userId에 해당하는 사용자 정보를 데이터베이스에서 조회합니다.
        return userRepository.findById(userId).orElse(null);
    }
    public Long getLoggedInUserId(UserDetailsImpl userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && userDetails != null) {
            // 현재 로그인한 사용자의 UserDetails 객체와 매개변수로 전달된 UserDetails 객체를 비교하여 일치하는 경우 사용자 ID를 반환합니다.
            if (authentication.getPrincipal().equals(userDetails)) {
                // 여기서는 UserDetails에 사용자 ID가 포함되어 있다고 가정합니다.
                // 만약 UserDetails에 사용자 ID가 포함되어 있지 않다면 사용자의 정보를 저장하는 다른 방법을 사용해야 합니다.
                return userDetails.getId();
            }
        }

        return null; // 현재 로그인한 사용자가 없거나 인증되지 않았을 경우 또는 UserDetails가 null인 경우
    }

    @Transactional(readOnly = true)
    public Long findIdByUsername(String username) {
        String jpql = "SELECT u.id FROM users u WHERE u.username = :username";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("username", username);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username);
        }
    }
    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Transactional
    public String modifyName(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 사용자의 이름 가져오기
        String originalName = user.getName();

        // 이름이 null이 아니고 길이가 1 이상인 경우만 처리
        if (originalName != null && originalName.length() > 1) {
            // 첫 글자를 제외한 나머지 글자를 "xx" 또는 "oo"로 바꾸기
            String modifiedName = originalName.charAt(0) + "X".repeat(originalName.length() - 1);
            return modifiedName;
        } else if (originalName != null && originalName.length() == 1) {
            // 이름이 한 글자일 경우 그대로 반환
            return originalName;
        } else {
            throw new IllegalArgumentException("Name is invalid or empty for user with id: " + userId);
        }
    }

    @Transactional
    public boolean userIsDoctor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        String userRole = String.valueOf(user.getRole());

        return userRole.equals("DOCTOR") || userRole.equals("ADMIN");
    }

//    @Transactional
//    public Long findUserIdByUserName(String userName){
//        User user = userRepository.findByUsername(userName)
//                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userName));
//
//        return user.getId();
//    }
}
