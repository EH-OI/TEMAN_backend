package TEMAN.controller;

import TEMAN.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts/{postId}/likes")
public class PostLikeController {
    private final PostLikeService postLikeService;

    @PostMapping
    public ResponseEntity<String> toggleLike(@PathVariable Long postId, Principal principal) {
        String resultMessage = postLikeService.toggleLike(postId, principal.getName());
        return ResponseEntity.ok(resultMessage);
    }
}
