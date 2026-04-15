package TEMAN.repository;

import TEMAN.domain.Post;
import TEMAN.domain.PostLike;
import TEMAN.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    // 유저가 특정 게시물에 이미 좋아요를 눌렀는지 확인
    boolean existsByUserAndPost(User user, Post post);

    // 좋아요 취소를 위한 좋아요 기록 찾기
    Optional<PostLike> findByUserAndPost(User user, Post post);
}
