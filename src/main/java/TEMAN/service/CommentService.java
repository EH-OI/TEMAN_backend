package TEMAN.service;

import TEMAN.domain.Comment;
import TEMAN.domain.Post;
import TEMAN.domain.User;
import TEMAN.dto.request.CommentRequestDto;
import TEMAN.dto.response.CommentResponseDto;
import TEMAN.repository.CommentRepository;
import TEMAN.repository.PostRepository;
import TEMAN.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 댓글 작성
    public Long createComment(Long postId, String email, CommentRequestDto commentRequestDto) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. "));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다. "));

        // 도메인 필드
        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content(commentRequestDto.content())
                .build();

        commentRepository.save(comment);

        post.addCommentCount(); // 게시글 댓글 수 1 증가 - 더티체킹
        return comment.getId();
    }

    // 특정 게시글 댓글 페이징 조회
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getComments(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다. "));

        return commentRepository.findAllByPostOrderByCreatedAtAsc(post, pageable).map(CommentResponseDto::fromEntity);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, String email) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다. "));

        if (!comment.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인이 작성한 댓글만 삭제할 수 있습니다. ");
        }

        comment.getPost().subCommentCount();
        commentRepository.delete(comment);
    }
}
