package TEMAN.dto.response;

import TEMAN.domain.Post;
import TEMAN.domain.User;
import TEMAN.domain.enums.PostCategoryEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PostResponseDto (
        Long postId,

        String userName,
        String userProfileImageUrl,

        String postEnumCategory,
        String title,
        String content,
        List<String> imageUrl,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt,

        Boolean isLiked,

        Boolean isAnonymous,
        String subCategory,
        LocalDateTime meetupDateTime,
        LocalDate eventDate,
        Boolean participantManagement,
        String location,
        Double latitude,
        Double longitude,
        Integer maxParticipants,
        Long price,
        String salary,
        Boolean isNotice,
        String meetupStatus

) {
    public static PostResponseDto fromEntity(Post post, boolean isLiked){

        boolean anon = post.getIsAnonymous() != null && post.getIsAnonymous();
        String displayUserName = anon ? "Anonymous" : post.getUser().getFullName();
        String displayProfileImage = anon ? null : post.getUser().getProfileImageUrl();

        String subCategory = null;
        if(post.getMeetupsCategoryEnum() != null) subCategory = post.getMeetupsCategoryEnum().name();
        else if(post.getEventsCategoryEnum() != null) subCategory = post.getEventsCategoryEnum().name();
        else if(post.getQnACategoryEnum() != null) subCategory = post.getQnACategoryEnum().name();
        else if(post.getProductCategoryEnum() != null) subCategory = post.getProductCategoryEnum().name();
        else if(post.getJobTypeEnum() != null) subCategory = post.getJobTypeEnum().name();

        String status = (post.getMeetupStatusEnum() != null) ? post.getMeetupStatusEnum().name() : null;

        return new PostResponseDto(
                post.getId(),
                displayUserName,
                displayProfileImage,
                post.getPostCategoryEnum().name(),
                post.getTitle(),
                post.getContent(),
                post.getImageUrl(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedAt(),
                isLiked,
                anon,
                subCategory,
                post.getMeetupDateTime(),
                post.getEventDate(),
                post.getParticipantManagement(),
                post.getLocation(),
                post.getLatitude(),
                post.getLongitude(),
                post.getMaxParticipants(),
                post.getPrice(),
                post.getSalary(),
                post.getIsNotice(),
                status
        );
    }
}
