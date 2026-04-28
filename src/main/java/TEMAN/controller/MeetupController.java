package TEMAN.controller;

import TEMAN.service.MeetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/participants")
public class MeetupController {
    private final MeetupService meetupService;

    @PostMapping
    public ResponseEntity<String> joinMeetup(@PathVariable Long postId, Principal principal) {
        String resultMessage = meetupService.joinMeetup(postId, principal.getName());
        return ResponseEntity.ok(resultMessage);
    }

    @PostMapping("/{participantId}/approve")
    public ResponseEntity<String> approveParticipant(
            @PathVariable Long postId,
            @PathVariable Long participantId, // 신청자 특정 ID
            Principal principal) {

        String resultMessage = meetupService.approveParticipant(postId, participantId, principal.getName());
        return ResponseEntity.ok(resultMessage);
    }
}
