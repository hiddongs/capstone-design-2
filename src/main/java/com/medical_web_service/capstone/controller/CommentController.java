package com.medical_web_service.capstone.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medical_web_service.capstone.dto.CommentDto;
import com.medical_web_service.capstone.dto.CommentResponseDto;
import com.medical_web_service.capstone.entity.Comment;
import com.medical_web_service.capstone.service.CommentService;
import com.medical_web_service.capstone.service.UserDetailsImpl;
import com.medical_web_service.capstone.service.UserService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/doctor")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    /** 생성 기능 **/
    @PostMapping
    public ResponseEntity<Comment> createComment(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "boardId") Long boardId,
            @RequestBody CommentDto.CreateCommentDto createCommentDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long loggedInUserId = userService.getLoggedInUserId(userDetails);

        if (!userId.equals(loggedInUserId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Comment createdComment = commentService.createComment(userId, boardId, createCommentDto);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    /** 댓글 불러오기 **/
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<CommentResponseDto>> readComments(
            @PathVariable(name = "boardId") Long boardId
    ) {
        return ResponseEntity.ok(commentService.readComments(boardId));
    }

    /** 수정 기능 **/
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "boardId") Long boardId,
            @PathVariable(name = "commentId") Long commentId,
            @RequestBody CommentDto.UpdateCommentDto updateCommentDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        Long loggedInUserId = userService.getLoggedInUserId(userDetails);

        if (!userId.equals(loggedInUserId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Comment updatedComment = commentService.updateComment(userId, boardId, commentId, updateCommentDto);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    /** 삭제 기능 **/
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable(name = "commentId") Long commentId,
            @RequestParam(name = "userId") Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long loggedInUserId = userService.getLoggedInUserId(userDetails);

        if (!userId.equals(loggedInUserId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
