package TEMAN.dto.request;

import TEMAN.domain.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PostCreateRequestDto (
        @NotNull(message = "카테고리를 선택해주세요")
        PostCategoryEnum postCategoryEnum,

        @NotBlank(message = "제목을 입력해주세요")
        String title,

        @NotBlank(message = "내용을 입력해주세요")
        String content,

        @Size(max = 5, message = "이미지 최대 5장까지 업로드 가능")
        List<String> imageUrl,

        Boolean isAnonymous,

        MeetupsCategoryEnum meetupsCategoryEnum,
        EventsCategoryEnum eventsCategoryEnum,
        QnACategoryEnum qnACategoryEnum,
        ProductCategoryEnum productCategoryEnum,
        JobTypeEnum jobTypeEnum,

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
        MeetupStatusEnum meetupStatusEnum
) {
}
