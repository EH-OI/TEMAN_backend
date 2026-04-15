package TEMAN.dto.response;

import TEMAN.domain.Comment;
import TEMAN.domain.User;


import java.time.LocalDateTime;


public record CommentResponseDto (
        Long commentId,
        String userName,
        String content,
        LocalDateTime createdAt,
        String userProfileImageUrl

){
    public static CommentResponseDto fromEntity(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getUser().getFullName(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUser().getProfileImageUrl()
        );
    }
}
