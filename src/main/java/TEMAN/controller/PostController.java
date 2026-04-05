package TEMAN.controller;

import TEMAN.domain.enums.PostCategoryEnum;
import TEMAN.dto.request.PostCreateRequestDto;
import TEMAN.dto.response.PostResponseDto;
import TEMAN.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    //게시글 작성
    @PostMapping
    public ResponseEntity<String> createPost(Principal principal, @Valid @RequestBody PostCreateRequestDto postCreateRequestDto) {
        Long postId = postService.createPost(principal.getName(), postCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("게시글이 작성되었습니다.");
    }

    //홈화면 전체 게시물
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getAllPost(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponseDto> posts = postService.getAllPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    //카테고리별 게시글
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<PostResponseDto>> getPostsByCategory(
            @PathVariable("category") PostCategoryEnum postCategoryEnum,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostResponseDto> posts = postService.getPostCategory(postCategoryEnum, pageable);
        return ResponseEntity.ok(posts);
    }
}
