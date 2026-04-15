package TEMAN.service;

import TEMAN.domain.Post;
import TEMAN.domain.PostLike;
import TEMAN.domain.User;
import TEMAN.repository.CommentRepository;
import TEMAN.repository.PostLikeRepository;
import TEMAN.repository.PostRepository;
import TEMAN.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor

public class PostLikeService {
    public final UserRepository userRepository;
    public final PostRepository postRepository;
    public final PostLikeRepository postLikeRepository;

    public String toggleLike(Long postId, String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. "));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다. "));

        Optional<PostLike> existingLike = postLikeRepository.findByUserAndPost(user, post);

        // 이미 좋아요 누른 경우 -> 좋아요 취소
        if(existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            post.subLikeCount();

            return "좋아요가 취소되었습니다. ";
        } else {
            // 좋아요 누르지 않은 경우 -> 좋아요 추가
            PostLike newLike = PostLike.builder()
                    .user(user)
                    .post(post)
                    .build();
            postLikeRepository.save(newLike);
            post.addLikeCount();
            return "좋아요를 눌렀습니다. ";
        }
    }
}
