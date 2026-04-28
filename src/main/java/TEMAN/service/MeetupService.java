package TEMAN.service;

import TEMAN.domain.MeetupParticipant;
import TEMAN.domain.Post;
import TEMAN.domain.User;
import TEMAN.domain.enums.MeetupStatusEnum;
import TEMAN.domain.enums.ParticipantStatusEnum;
import TEMAN.domain.enums.PostCategoryEnum;
import TEMAN.repository.MeetupParticipantRepository;
import TEMAN.repository.PostRepository;
import TEMAN.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetupService {
    private final MeetupParticipantRepository meetupParticipantRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 모임 참여 신청
    public String joinMeetup(Long postId, String email) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. "));
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. "));

        if(post.getPostCategoryEnum() != PostCategoryEnum.MEETUPS) {
            throw new IllegalArgumentException("Meetups 글에만 참여할 수 있습니다. ");
        }
        if(post.getMeetupStatusEnum() == MeetupStatusEnum.CLOSED) {
            throw new IllegalArgumentException("이미 모집이 마감된 모임입니다. ");
        }
        if(meetupParticipantRepository.existsByPostAndUser(post, user)) {
            throw new IllegalArgumentException("이미 참여 신청을 한 모임입니다. ");
        }
        if(post.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인이 생성한 모임에 신청할 수 없습니다. ");
        }

        // 현재 승인된 인원 수 확인
        long approvedCount = meetupParticipantRepository.countByPostAndParticipantStatusEnum(post, ParticipantStatusEnum.APPROVED);
        if(approvedCount >= post.getMaxParticipants()) {
            post.closeMeetup(); // 정원 초과시 닫음
            throw new IllegalArgumentException("모집 정원이 초과되었습니다. ");
        }

        // participantManagement 설정에 따른 초기 상태 결정
        ParticipantStatusEnum initialStatus = post.getParticipantManagement()
                ? ParticipantStatusEnum.PENDING // 승인제(대기)
                : ParticipantStatusEnum.APPROVED; // 선착순(즉시확정)

        MeetupParticipant meetupParticipant = MeetupParticipant.builder()
                .post(post)
                .user(user)
                .participantStatusEnum(initialStatus)
                .build();
        meetupParticipantRepository.save(meetupParticipant);

        // 선책순(즉시 승인) + 정원초과 -> 마감
        if(initialStatus == ParticipantStatusEnum.APPROVED && (approvedCount + 1) >= post.getMaxParticipants()) {
            post.closeMeetup();
        }
        return initialStatus == ParticipantStatusEnum.APPROVED
                ? "모임 참여가 확정되었습니다. "
                : "참여 신청이 완료되었습니다. 방장의 승인을 기다려주세요. ";

    }

    // 방장이 참여자 승인
    public String approveParticipant(Long postId, Long participantId, String email) {
        Post post = postRepository.findById(postId).orElseThrow(()-> new IllegalArgumentException("존재하지 않는 게시글입니다. "));
        MeetupParticipant meetupParticipant = meetupParticipantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청 내역입니다. "));

        if(!post.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("방장만 승인할 수 있습니다. ");
        }
        if(meetupParticipant.getParticipantStatusEnum() != ParticipantStatusEnum.PENDING) {
            throw new IllegalArgumentException("대기 중인 신청건만 승인할 수 있습니다. ");
        }

        // 정원 초과 확인
        long approvedCount = meetupParticipantRepository.countByPostAndParticipantStatusEnum(post, ParticipantStatusEnum.APPROVED);
        if(approvedCount >= post.getMaxParticipants()) {
            post.closeMeetup();
            throw new IllegalArgumentException("모집 정원이 가득 차서 더 이상 승인할 수 없습니다. ");
        }

        // 승인 처리
        meetupParticipant.updateStatus(ParticipantStatusEnum.APPROVED);

        // 방금 승인해서 정원 초과시 모집 마감
        if((approvedCount + 1) >= post.getMaxParticipants()) {
            post.closeMeetup();
        }

        return meetupParticipant.getUser().getFullName() + "님의 참여를 승인했습니다. ";
    }


}
