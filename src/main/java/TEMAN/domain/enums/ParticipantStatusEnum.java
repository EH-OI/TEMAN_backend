package TEMAN.domain.enums;

public enum ParticipantStatusEnum {
    PENDING, // 대기 중(방장 승인 필요)
    APPROVED, // 참여 확정
    REJECTED,  // 거절됨
    CANCELED // 본인 취소
}
