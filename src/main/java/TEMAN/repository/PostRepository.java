package TEMAN.repository;

import TEMAN.domain.Post;
import TEMAN.domain.User;
import TEMAN.domain.enums.PostCategoryEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    //All
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    //카테고리
    Page<Post> findAllByPostCategoryEnumOrderByCreatedAtDesc(PostCategoryEnum postCategoryEnum, Pageable pageable);

    //my post
    Page<Post> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
