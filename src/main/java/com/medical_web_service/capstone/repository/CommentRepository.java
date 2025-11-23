package com.medical_web_service.capstone.repository;

import com.medical_web_service.capstone.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
 
	
	 List<Comment> findByBoard_Id(Long boardId);
}
