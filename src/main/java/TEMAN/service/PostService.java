package TEMAN.service;

import TEMAN.domain.Post;
import TEMAN.domain.User;
import TEMAN.domain.enums.PostCategoryEnum;
import TEMAN.dto.request.PostCreateRequestDto;
import TEMAN.dto.response.PostResponseDto;
import TEMAN.repository.PostLikeRepository;
import TEMAN.repository.PostRepository;
import TEMAN.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final EntityManagerFactoryInfo entityManagerFactoryInfo;

    // 양식 검증
    private void validateCategoryFields(PostCategoryEnum postCategoryEnum, PostCreateRequestDto postCreateRequestDto) {
        switch (postCategoryEnum) {
            case GENERAL:
                if (postCreateRequestDto.isAnonymous() == null) throw new IllegalArgumentException("익명 여부를 선택해주세요.");
                break;
            case MEETUPS:
                if (postCreateRequestDto.meetupsCategoryEnum() == null) throw new IllegalArgumentException("Meetup 세부 카테고리를 선택해주세요.");
                if (postCreateRequestDto.location() == null || postCreateRequestDto.meetupDateTime() == null || postCreateRequestDto.maxParticipants() == null) {
                    throw new IllegalArgumentException("Meetup 글에는 장소, 일시, 최대 인원 정보가 필수입니다.");
                }
                if (postCreateRequestDto.participantManagement() == null) throw new IllegalArgumentException("승인제 여부를 설정해주세요.");
                break;
            case EVENTS:
                if (postCreateRequestDto.eventsCategoryEnum() == null) throw new IllegalArgumentException("Event 세부 카테고리를 선택해주세요.");
                if (postCreateRequestDto.eventDate() == null) throw new IllegalArgumentException("이벤트 날짜를 입력해주세요.");
                if (postCreateRequestDto.isAnonymous() == null) throw new IllegalArgumentException("익명 여부를 선택해주세요.");
                break;
            case QnA:
                if (postCreateRequestDto.qnACategoryEnum() == null) throw new IllegalArgumentException("Q&A 세부 카테고리를 선택해주세요.");
                if (postCreateRequestDto.isAnonymous() == null) throw new IllegalArgumentException("익명 여부를 선택해주세요.");
                break;
            case MARKET:
                if (postCreateRequestDto.productCategoryEnum() == null) throw new IllegalArgumentException("상품 카테고리를 선택해주세요.");
                if (postCreateRequestDto.price() == null) throw new IllegalArgumentException("가격을 입력해주세요.");
                break;
            case JOBS:
                if (postCreateRequestDto.jobTypeEnum() == null) throw new IllegalArgumentException("근무 형태(Job Type)를 선택해주세요.");
                if (postCreateRequestDto.salary() == null || postCreateRequestDto.location() == null) {
                    throw new IllegalArgumentException("구인 글에는 급여와 위치 정보가 필수입니다.");
                }
                break;
        }
    }

    //게시글 작성
    public Long createPost(String email, PostCreateRequestDto postCreateRequestDto) {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. "));

        validateCategoryFields(postCreateRequestDto.postCategoryEnum(), postCreateRequestDto);

        Post post = Post.builder()
                .user(user)
                .title(postCreateRequestDto.title())
                .content(postCreateRequestDto.content())
                .postCategoryEnum(postCreateRequestDto.postCategoryEnum())
                .imageUrl(postCreateRequestDto.imageUrl())
                .isAnonymous(postCreateRequestDto.isAnonymous())
                .meetupsCategoryEnum(postCreateRequestDto.meetupsCategoryEnum())
                .eventsCategoryEnum(postCreateRequestDto.eventsCategoryEnum())
                .qnACategoryEnum(postCreateRequestDto.qnACategoryEnum())
                .productCategoryEnum(postCreateRequestDto.productCategoryEnum())
                .jobTypeEnum(postCreateRequestDto.jobTypeEnum())
                .meetupDateTime(postCreateRequestDto.meetupDateTime())
                .eventDate(postCreateRequestDto.eventDate())
                .participantManagement(postCreateRequestDto.participantManagement())
                .location(postCreateRequestDto.location())
                .latitude(postCreateRequestDto.latitude())
                .longitude(postCreateRequestDto.longitude())
                .maxParticipants(postCreateRequestDto.maxParticipants())
                .price(postCreateRequestDto.price())
                .salary(postCreateRequestDto.salary())
                .isNotice(postCreateRequestDto.isNotice())
                .meetupStatusEnum(postCreateRequestDto.meetupStatusEnum())
                .build();

        return postRepository.save(post).getId();
    }

    // 전체 게시글
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getAllPosts(Pageable pageable, String email) {
        User user = (email != null) ? userRepository.findUserByEmail(email).orElse(null) : null;
        return postRepository.findAllByOrderByIsNoticeDescCreatedAtDesc(pageable)
                .map(post -> {
                    boolean isLiked = (user != null) && postLikeRepository.existsByUserAndPost(user, post);
                    return PostResponseDto.fromEntity(post, isLiked);
                });
    }

    // 홈(카테고리)
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPostCategory(PostCategoryEnum postCategoryEnum, Pageable pageable, String email){
        User user = (email !=null) ? userRepository.findUserByEmail(email).orElse(null) : null;
        return postRepository.findAllByPostCategoryEnumOrderByIsNoticeDescCreatedAtDesc(postCategoryEnum, pageable)
                .map(post -> {
                    boolean isLiked = (user != null) && postLikeRepository.existsByUserAndPost(user, post);
                    return PostResponseDto.fromEntity(post, isLiked);
                });
    }

    //게시글 상세조회
    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long postId, String email) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다. "));

        User user = (email != null) ? userRepository.findUserByEmail(email).orElse(null) : null;
        boolean isLiked = (user != null) && postLikeRepository.existsByUserAndPost(user, post);
        return PostResponseDto.fromEntity(post, isLiked);
    }

    //게시글 수정
    @Transactional
    public Long updatePost(Long postId, String email, PostCreateRequestDto postCreateRequestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다. "));

        //보안
        if(!post.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인이 작성한 글만 수정할 수 있습니다. ");
        }

        validateCategoryFields(postCreateRequestDto.postCategoryEnum(), postCreateRequestDto);

        //Dirty checking
        post.updatePost(
                postCreateRequestDto.postCategoryEnum(),
                postCreateRequestDto.title(),
                postCreateRequestDto.content(),
                postCreateRequestDto.imageUrl(),
                postCreateRequestDto.isAnonymous(),
                postCreateRequestDto.meetupsCategoryEnum(),
                postCreateRequestDto.eventsCategoryEnum(),
                postCreateRequestDto.qnACategoryEnum(),
                postCreateRequestDto.productCategoryEnum(),
                postCreateRequestDto.jobTypeEnum(),
                postCreateRequestDto.meetupDateTime(),
                postCreateRequestDto.eventDate(),
                postCreateRequestDto.participantManagement(),
                postCreateRequestDto.location(),
                postCreateRequestDto.latitude(),
                postCreateRequestDto.longitude(),
                postCreateRequestDto.maxParticipants(),
                postCreateRequestDto.price(),
                postCreateRequestDto.salary(),
                postCreateRequestDto.isNotice(),
                postCreateRequestDto.meetupStatusEnum()
        );

        return post.getId();
    }

    //게시글 삭제
    @Transactional
    public void deletePost(Long postId, String email) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다. "));

        //보안
        if(!post.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인이 작성한 글만 수정할 수 있습니다. ");
        }

        postRepository.delete(post);
    }
}
