package TEMAN.dto.response;

import TEMAN.domain.Post;
import TEMAN.domain.User;
import TEMAN.domain.enums.PostCategoryEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PostResponseDto (
        Long postId,

        String userName,
        String userProfileImageUrl,

        String postEnumCategory,
        String title,
        String content,
        String imageUrl,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt,

        Boolean isLiked,

        LocalDateTime meetupDateTime,
        String location,
        Integer maxParticipants,
        Long price,
        String companyName,
        String salary

) {
    public static PostResponseDto fromEntity(Post post, boolean isLiked){
        return new PostResponseDto(
                post.getId(),
                post.getUser().getFullName(), // User 엔티티에서 작성자 이름 빼오기
                post.getUser().getProfileImageUrl(), // User 엔티티에서 작성자 프사 빼오기
                post.getPostCategoryEnum().name(),
                post.getTitle(),
                post.getContent(),
                post.getImageUrl(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedAt(),
                isLiked,
                post.getMeetupDateTime(),
                post.getLocation(),
                post.getMaxParticipants(),
                post.getPrice(),
                post.getCompanyName(),
                post.getSalary()
        );
    }
}
