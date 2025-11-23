package com.medical_web_service.capstone.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.medical_web_service.capstone.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Override
    Page<Board> findAll(Pageable pageable);

    void deleteById(Long boardId);
    @Query("SELECT b FROM Board b LEFT JOIN Comment c ON b.id = c.board.id " +
    	       "WHERE c.id IS NULL AND b.department = :department")
    	List<Board> findUnansweredBoardsByDepartment(String department);
    List<Board> findByDepartment(String department);
    List<Board> findByTitleContainingOrContentContaining(String title, String content);
}

