package TEMAN.repository;

import TEMAN.domain.MeetupParticipant;
import TEMAN.domain.Post;
import TEMAN.domain.User;
import TEMAN.domain.enums.ParticipantStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetupParticipantRepository extends JpaRepository<MeetupParticipant, Long> {
    // 유저가 이미 이 모임에 신청했는지 여부
    boolean existsByPostAndUser(Post post, User user);

    // 이 모임에서 참여 확정(APPROVED)된 사람 수가 몇 명인지 카운트 (정원초과 방지)
    long countByPostAndParticipantStatusEnum(Post post, ParticipantStatusEnum participantStatusEnum);
}
