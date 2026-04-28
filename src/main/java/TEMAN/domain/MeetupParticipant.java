package TEMAN.domain;

import TEMAN.domain.enums.ParticipantStatusEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meetup_participants")
@Getter
@NoArgsConstructor
public class MeetupParticipant extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantStatusEnum participantStatusEnum;

    @Builder
    public MeetupParticipant(Post post, User user, ParticipantStatusEnum participantStatusEnum) {
        this.post = post;
        this.user = user;
        this.participantStatusEnum = participantStatusEnum;
    }

    // 상태 변경 (승인/거절)
    public void updateStatus(ParticipantStatusEnum participantStatusEnum) {
        this.participantStatusEnum = participantStatusEnum;
    }
}
