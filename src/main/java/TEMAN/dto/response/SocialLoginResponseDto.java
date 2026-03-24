package TEMAN.dto.response;

public record SocialLoginResponseDto(
        boolean isNewUser,   // true: 신규 유저(회원가입 창으로 이동), false: 기존 유저(로그인 성공)
        String email,        // 신규 유저일 경우, 회원가입 폼에 미리 이메일을 채워주기 위해 반환
        String socialId,     // 구글/애플의 고유 식별자 (가입 시 필요함)
        String accessToken   // 기존 유저일 경우 발급되는 JWT 토큰
) {}