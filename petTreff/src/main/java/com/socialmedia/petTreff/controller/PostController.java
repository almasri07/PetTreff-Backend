package com.socialmedia.petTreff.controller;

import com.socialmedia.petTreff.dto.*;
import com.socialmedia.petTreff.entity.Comment;
import com.socialmedia.petTreff.entity.Post;
import com.socialmedia.petTreff.entity.User;
import com.socialmedia.petTreff.mapper.CommentMapper;
import com.socialmedia.petTreff.mapper.PostMapper;
import com.socialmedia.petTreff.repository.UserRepository;
import com.socialmedia.petTreff.security.UserPrincipal;
import com.socialmedia.petTreff.service.LikeService;
import com.socialmedia.petTreff.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Validated
public class PostController {

    private final PostService postService;

    private final LikeService likeService;

    private final UserRepository userRepository;

    @Autowired
    public PostController(PostService postService, UserRepository userRepository, LikeService likeService) {
        this.postService = postService;
        this.userRepository = userRepository;
        this.likeService = likeService;
    }

    @GetMapping
    public ResponseEntity<Page<PostDTO>> getAllPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @GetMapping("/friends")
    public ResponseEntity<Page<PostDTO>> getFriendsPosts(@AuthenticationPrincipal UserPrincipal actualUser
            , Pageable pageable) {
        return ResponseEntity.ok(postService.getFriendsPosts(actualUser.getId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {

        PostDTO dto = postService.getPostById(id);

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody CreatePostDTO req,
            @AuthenticationPrincipal UserPrincipal actualUser) {


        PostDTO post = postService.createPost(req, actualUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id, @RequestBody UpdatePostDTO post) {
        PostDTO updatedPost = postService.updatePost(id, post);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<CommentDTO>> getPostComments(@PathVariable Long id,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(postService.getPostComments(id, pageable));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDTO> addCommentToPost(@PathVariable Long postId, @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        PostDTO postDTO = postService.getPostById(postId);
        Post post = PostMapper.toEntity(postDTO, userRepository);
        Comment comment = CommentMapper.toEntity(commentDTO);
        comment.setPost(post);
        CommentDTO savedComment = postService.createComment(postId, commentDTO, userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentDTO commentDTO) {

        CommentDTO updatedComment = postService.updateComment(postId, commentId, commentDTO);

        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        postService.deleteComment(postId, commentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<LikeDTO> likePost(@PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        LikeDTO likeDTO = likeService.createLike(postId, userPrincipal);
        return ResponseEntity.ok(likeDTO);
    }

    @PostMapping("/{postId}/unlike")
    public ResponseEntity<String> unlikePost(@PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        likeService.deleteLike(postId, userPrincipal.getId());
        return ResponseEntity.ok("Post unliked");
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<List<User>> getUsersWhoLiked(@PathVariable Long postId) {

        return ResponseEntity.ok(postService.getUsersWhoLiked(postId));
    }

    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<Object> getLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getLikeCount(postId));
    }
}
