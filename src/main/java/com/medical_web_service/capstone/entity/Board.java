package com.medical_web_service.capstone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column
    private String writer;

    @Column
    private LocalDateTime postedTime;

    @Column
    private LocalDateTime updatedTime;
    @Column
    private String symptom;     // 예: "두통, 열"
    @Column
    private String department;  // 예: "이비인후과"
    @Column
    private boolean anonymous;  // true면 writer 숨김
    @PrePersist
    protected void onCreate() {
        postedTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }

}
