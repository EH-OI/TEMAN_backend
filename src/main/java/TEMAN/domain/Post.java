package TEMAN.domain;

import TEMAN.domain.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(columnDefinition = "TEXT")
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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_url")
    private List<String> imageUrl = new ArrayList<>();


    @Column
    private Boolean isAnonymous;

    @Enumerated(EnumType.STRING)
    private MeetupsCategoryEnum meetupsCategoryEnum;

    @Enumerated(EnumType.STRING)
    private EventsCategoryEnum eventsCategoryEnum;

    @Enumerated(EnumType.STRING)
    private QnACategoryEnum qnACategoryEnum;

    @Enumerated(EnumType.STRING)
    private ProductCategoryEnum productCategoryEnum;

    @Enumerated(EnumType.STRING)
    private JobTypeEnum jobTypeEnum;



    @Column
    private LocalDateTime meetupDateTime;
    @Column
    private LocalDate eventDate;
    @Column
    private Boolean participantManagement;


    // 추후 지도 api 사용시
    @Column
    private String location;
    @Column
    private Double latitude; // 위도
    @Column
    private Double longitude; // 경도


    @Column
    private Integer maxParticipants;
    @Column
    private Long price;
    @Column
    private String salary;

    @Column(nullable = false)
    private Boolean isNotice = false;

    @Enumerated(EnumType.STRING)
    @Column
    private MeetupStatusEnum meetupStatusEnum;


    @Builder
    public Post(User user, String title, String content, PostCategoryEnum postCategoryEnum, int likeCount, int commentCount, List<String> imageUrl, Boolean isAnonymous, MeetupsCategoryEnum meetupsCategoryEnum, EventsCategoryEnum eventsCategoryEnum, QnACategoryEnum qnACategoryEnum, ProductCategoryEnum productCategoryEnum, JobTypeEnum jobTypeEnum, LocalDateTime meetupDateTime, LocalDate eventDate, Boolean participantManagement, String location, Double latitude, Double longitude, Integer maxParticipants, Long price, String salary, Boolean isNotice, MeetupStatusEnum meetupStatusEnum) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.postCategoryEnum = postCategoryEnum;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.imageUrl = (imageUrl != null) ? imageUrl : new ArrayList<>();
        this.isAnonymous = (isAnonymous != null) ? isAnonymous : false;
        this.meetupsCategoryEnum = meetupsCategoryEnum;
        this.eventsCategoryEnum = eventsCategoryEnum;
        this.qnACategoryEnum = qnACategoryEnum;
        this.productCategoryEnum = productCategoryEnum;
        this.jobTypeEnum = jobTypeEnum;
        this.meetupDateTime = meetupDateTime;
        this.eventDate = eventDate;
        this.participantManagement = participantManagement;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.maxParticipants = maxParticipants;
        this.price = price;
        this.salary = salary;
        this.isNotice = (isNotice != null) ? isNotice : false;
        if (postCategoryEnum == PostCategoryEnum.MEETUPS || postCategoryEnum == PostCategoryEnum.MEETUPS) {
            this.meetupStatusEnum = (meetupStatusEnum != null) ? meetupStatusEnum : MeetupStatusEnum.RECRUITING;
        } else {
            this.meetupStatusEnum = null;
        }
    }

    //수정 로직
    public void updatePost(PostCategoryEnum postCategoryEnum, String title, String content,
                           List<String> imageUrl, Boolean isAnonymous,
                           MeetupsCategoryEnum meetupsCategoryEnum, EventsCategoryEnum eventsCategoryEnum,
                           QnACategoryEnum qnACategoryEnum, ProductCategoryEnum productCategoryEnum, JobTypeEnum jobTypeEnum,
                           LocalDateTime meetupDateTime, LocalDate eventDate, Boolean participantManagement,
                           String location, Double latitude, Double longitude,
                           Integer maxParticipants, Long price, String salary, Boolean isNotice, MeetupStatusEnum meetupStatusEnum) {

        this.postCategoryEnum = postCategoryEnum;
        this.title = title;
        this.content = content;

        this.imageUrl.clear();
        if(imageUrl != null) {
            this.imageUrl.addAll(imageUrl);
        }

        this.isAnonymous = (isAnonymous != null) ? isAnonymous : false;

        this.meetupsCategoryEnum = meetupsCategoryEnum;
        this.eventsCategoryEnum = eventsCategoryEnum;
        this.qnACategoryEnum = qnACategoryEnum;
        this.productCategoryEnum = productCategoryEnum;
        this.jobTypeEnum = jobTypeEnum;

        this.meetupDateTime = meetupDateTime;
        this.eventDate = eventDate;
        this.participantManagement = (participantManagement != null) ? participantManagement : false;

        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;

        this.maxParticipants = maxParticipants;
        this.price = price;
        this.salary = salary;

        if (isNotice != null) this.isNotice = isNotice;
        if (meetupStatusEnum != null) this.meetupStatusEnum = meetupStatusEnum;
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
