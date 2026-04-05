package TEMAN.dto.request;

import TEMAN.domain.enums.PostCategoryEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PostCreateRequestDto (
        @NotNull(message = "카테고리를 선택해주세요")
        PostCategoryEnum postCategoryEnum,

        @NotBlank(message = "제목을 입력해주세요")
        String title,

        @NotBlank(message = "내용을 입력해주세요")
        String content,

        String imageUrl,

        LocalDateTime meetupDateTime,
        String location,
        Integer maxParticipants,
        Long price,
        String companyName,
        String salary
) {
}
