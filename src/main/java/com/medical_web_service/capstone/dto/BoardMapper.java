package com.medical_web_service.capstone.dto;

import com.medical_web_service.capstone.entity.Board;

public class BoardMapper {

    public static BoardDto.PostDetailsDTO toDto(Board board) {

        // 익명 처리 적용
        String writer = board.isAnonymous() ? "익명" : board.getWriter();
        return new BoardDto.PostDetailsDTO(
        	    board.getId(),
        	    board.getTitle(),
        	    board.getContent(),
        	    writer,
        	    board.getUser().getId(),
        	    board.getPostedTime(),
        	    board.getUpdatedTime(),
        	    board.getSymptom(),
        	    board.getDepartment(),
        	    board.isAnonymous()
        );
    }
}
