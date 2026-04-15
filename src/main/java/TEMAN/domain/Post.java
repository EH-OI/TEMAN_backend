package TEMAN.domain;

import TEMAN.domain.enums.PostCategoryEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@Getter

public class Post extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    @NotBlank
    private String title;

    @Column
    @NotBlank
    private String content;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private PostCategoryEnum postCategoryEnum;

    @Column
    @NotNull
    private int likeCount = 0;

    @Column
    @NotNull
    private int commentCount = 0;

    @Column
    private LocalDateTime meetupDateTime;

    @Column
    private String imageUrl;

    @Column
    private String location;

    @Column
    private Long price;

    @Column
    private String companyName;

    @Column
    private String salary;

    @Column
    private Integer maxParticipants;

    @Builder
    public Post(User user, String title, String content, PostCategoryEnum postCategoryEnum, int likeCount, int commentCount, LocalDateTime meetupDateTime, String imageUrl, String location, Long price, String companyName, String salary, Integer maxParticipants) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.postCategoryEnum = postCategoryEnum;
        this.meetupDateTime = meetupDateTime;
        this.imageUrl = imageUrl;
        this.location = location;
        this.price = price;
        this.companyName = companyName;
        this.salary = salary;
        this.maxParticipants = maxParticipants;
        //likeCount, commentCount는 생성시 0으로 고정되므로 제외
    }

    //수정 로직
    public void updatePost(PostCategoryEnum postCategoryEnum, String title, String content, String imageUrl, LocalDateTime meetupDateTime, String location, Integer maxParticipants, Long price, String companyName, String salary) {
        this.postCategoryEnum = postCategoryEnum;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.meetupDateTime = meetupDateTime;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.price = price;
        this.companyName = companyName;
        this.salary = salary;
    }

    //좋아요 수 조작
    public void addLikeCount() {
        this.likeCount++;
    }
    public void subLikeCount() {
        if(this.likeCount > 0) this.likeCount--;
    }
    public void addCommentCount() { this.commentCount++; }
    public void subCommentCount() { if(this.commentCount > 0 ) this.commentCount--;}
}
