package TEMAN.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@NoArgsConstructor
@Getter

public class Comment extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 누가 달았는지 - n:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 어느 게시글에 달았는지 - n:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column
    @NotNull
    String content;

    @Builder
    public Comment(User user, Post post, String content) {
        this.user = user;
        this.post = post;
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
