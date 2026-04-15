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

    //게시글 작성
    public Long createPost(String email, PostCreateRequestDto postCreateRequestDto) {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. "));

        Post post = Post.builder()
                .user(user)
                .title(postCreateRequestDto.title())
                .content(postCreateRequestDto.content())
                .postCategoryEnum(postCreateRequestDto.postCategoryEnum())
                .meetupDateTime(postCreateRequestDto.meetupDateTime())
                .imageUrl(postCreateRequestDto.imageUrl())
                .location(postCreateRequestDto.location())
                .price(postCreateRequestDto.price())
                .companyName(postCreateRequestDto.companyName())
                .salary(postCreateRequestDto.salary())
                .maxParticipants(postCreateRequestDto.maxParticipants())
                .build();

        return postRepository.save(post).getId();
    }

    //전체 게시글
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getAllPosts(Pageable pageable, String email) {
        User user = (email != null) ? userRepository.findUserByEmail(email).orElse(null) : null;
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(post -> {
                    boolean isLiked = (user != null) && postLikeRepository.existsByUserAndPost(user, post);
                    return PostResponseDto.fromEntity(post, isLiked);
                });
    }

    //홈(카테고리)
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPostCategory(PostCategoryEnum postCategoryEnum, Pageable pageable, String email){
        User user = (email !=null) ? userRepository.findUserByEmail(email).orElse(null) : null;
        return postRepository.findAllByPostCategoryEnumOrderByCreatedAtDesc(postCategoryEnum, pageable)
                .map(post -> {
                    boolean isLiked = (user != null) && postLikeRepository.existsByUserAndPost(user, post);
                    return PostResponseDto.fromEntity(post, isLiked);
                });
    }

    //게시글 상세조회
    @Transactional
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

        //Dirty checking
        post.updatePost(
                postCreateRequestDto.postCategoryEnum(),
                postCreateRequestDto.title(),
                postCreateRequestDto.content(),
                postCreateRequestDto.imageUrl(),
                postCreateRequestDto.meetupDateTime(),
                postCreateRequestDto.location(),
                postCreateRequestDto.maxParticipants(),
                postCreateRequestDto.price(),
                postCreateRequestDto.companyName(),
                postCreateRequestDto.salary()
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
