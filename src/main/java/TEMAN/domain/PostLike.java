package TEMAN.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_likes",
    // 한 유저가 같은 글에 좋아요를 여러 번 누르지 못하게 DB 차원에서 막는 제약조건
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "post_id"})
    })
@NoArgsConstructor
@Getter

public class PostLike extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Builder
    public PostLike(Post post, User user) {
        this.post = post;
        this.user = user;
    }
}
