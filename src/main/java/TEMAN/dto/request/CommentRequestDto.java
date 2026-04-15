package TEMAN.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentRequestDto (
        @NotBlank(message = "내용을 입력해주세요.")
        String content
) {
}
