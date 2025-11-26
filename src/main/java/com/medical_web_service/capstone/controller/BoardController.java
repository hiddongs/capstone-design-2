package com.medical_web_service.capstone.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.medical_web_service.capstone.dto.BoardDto;
import com.medical_web_service.capstone.dto.BoardMapper;
import com.medical_web_service.capstone.entity.Board;
import com.medical_web_service.capstone.repository.BoardRepository;
import com.medical_web_service.capstone.service.BoardService;
import com.medical_web_service.capstone.service.UserDetailsImpl;
import com.medical_web_service.capstone.service.UserService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final UserService userService;

    // 전체 게시글 조회 (페이징)
    @GetMapping
    public ResponseEntity<Page<BoardDto.PostDetailsDTO>> getAllBoards(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Board> boards = boardService.readAllBoard(pageable);
        Page<BoardDto.PostDetailsDTO> boardDtos = boards.map(BoardMapper::toDto);

        return ResponseEntity.ok(boardDtos);
    }

    //  게시글 단일 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDto.PostDetailsDTO> getBoardById(
            @PathVariable("boardId") Long boardId) {

        return boardService.getBoardById(boardId)
                .map(BoardMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 게시글 생성
    @PostMapping("/{userId}")
    public ResponseEntity<Void> createBoard(
            @PathVariable("userId") Long userId,
            @RequestBody BoardDto.CreateBoardDto boardDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long loggedInUserId = userService.getLoggedInUserId(userDetails);

        if (!userId.equals(loggedInUserId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boardService.createBoard(boardDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ✅ 게시글 수정
    @PutMapping("/{userId}/{boardId}")
    public ResponseEntity<Void> updateBoard(
            @PathVariable("userId") Long userId,
            @PathVariable("boardId") Long boardId,
            @RequestBody BoardDto.UpdateBoardDto boardDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long loggedInUserId = userService.getLoggedInUserId(userDetails);

        if (!userId.equals(loggedInUserId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boardService.updateBoard(userId, boardId, boardDto);
        return ResponseEntity.ok().build();
    }

    //  게시글 삭제
    @DeleteMapping("/{userId}/{boardId}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable("userId") Long userId,
            @PathVariable("boardId") Long boardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long loggedInUserId = userService.getLoggedInUserId(userDetails);

        if (!userId.equals(loggedInUserId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boardService.deleteBoard(boardId);
        return ResponseEntity.ok().build();
    }

    // 게시글 검색
    @GetMapping("/search")
    public ResponseEntity<List<Board>> getBoards(
            @RequestParam(name = "keyword", required = false) String keyword) {

        List<Board> list;

        if (keyword == null || keyword.isBlank()) {
            list = boardRepository.findAll();
        } else {
            list = boardRepository.findByTitleContainingOrContentContaining(keyword, keyword);
        }

        return ResponseEntity.ok(list);
    }
}
