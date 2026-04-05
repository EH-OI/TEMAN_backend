package TEMAN.service;

import TEMAN.domain.Post;
import TEMAN.domain.User;
import TEMAN.domain.enums.PostCategoryEnum;
import TEMAN.dto.request.PostCreateRequestDto;
import TEMAN.dto.response.PostResponseDto;
import TEMAN.repository.PostRepository;
import TEMAN.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

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
    public Page<PostResponseDto> getAllPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable).map(PostResponseDto::fromEntity);
    }

    //홈(카테고리)
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPostCategory(PostCategoryEnum postCategoryEnum, Pageable pageable){
        return postRepository.findAllByPostCategoryEnumOrderByCreatedAtDesc(postCategoryEnum, pageable)
                .map(PostResponseDto::fromEntity);
    }
}
