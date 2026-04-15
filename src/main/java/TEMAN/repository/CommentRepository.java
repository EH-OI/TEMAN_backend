package TEMAN.repository;

import TEMAN.domain.Comment;
import TEMAN.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 게시글의 댓글 목록을 시간 오름차순으로 페이징
    Page<Comment> findAllByPostOrderByCreatedAtAsc(Post post, Pageable pageable);
}
