package TEMAN.controller;

import TEMAN.dto.request.CommentRequestDto;
import TEMAN.dto.response.CommentResponseDto;
import TEMAN.service.CommentService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/post/{postId}")
    public ResponseEntity<String> createComment(Principal principal, @PathVariable Long postId, @Valid @RequestBody CommentRequestDto commentRequestDto) {
        Long commentId = commentService.createComment(postId, principal.getName(), commentRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("댓글이 작성되었습니다. ");
    }

    // 댓글 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<CommentResponseDto>> getComments(@PathVariable Long postId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<CommentResponseDto> comments = commentService.getComments(postId, pageable);
        return ResponseEntity.ok(comments);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId, Principal principal) {
        commentService.deleteComment(commentId, principal.getName());
        return ResponseEntity.ok("삭제되었습니다.");
    }
}
