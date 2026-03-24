package TEMAN.dto.response;

public record ErrorResponseDto(
        int status,
        String message
) {}