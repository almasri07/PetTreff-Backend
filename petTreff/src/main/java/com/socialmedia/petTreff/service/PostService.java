package com.socialmedia.petTreff.service;

import com.socialmedia.petTreff.dto.*;
import com.socialmedia.petTreff.entity.Comment;
import com.socialmedia.petTreff.entity.NotificationType;
import com.socialmedia.petTreff.entity.Post;
import com.socialmedia.petTreff.entity.User;
import com.socialmedia.petTreff.mapper.CommentMapper;
import com.socialmedia.petTreff.mapper.PostMapper;
import com.socialmedia.petTreff.repository.CommentRepository;
import com.socialmedia.petTreff.repository.PostLikeRepository;
import com.socialmedia.petTreff.repository.PostRepository;
import com.socialmedia.petTreff.repository.UserRepository;
import com.socialmedia.petTreff.security.InputSanitizer;
import com.socialmedia.petTreff.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostLikeRepository postlikeRepository;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final NotificationService notificationService;
    @Transactional(readOnly = true)
    public Page<PostDTO> getAllPosts(Pageable pageable) {

        return postRepository.findAll(pageable)
                .map(PostMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PostDTO> getFriendsPosts(Long userId, Pageable pageable) {

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!principal.getId().equals(userId)) {
            throw new AccessDeniedException("Not allowed to get feed of another user");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Set<Long> friendIds =  author.getFriends().stream().map(u -> u.getId()).collect(Collectors.toSet());

        return postRepository.findOwnAndFriendsPosts(userId, friendIds, pageable).map(PostMapper::toDto);



    }

    @Transactional(readOnly = true)
    public PostDTO getPostById(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return PostMapper.toDto(post);
    }

    @Transactional
    public PostDTO createPost(CreatePostDTO req, Long userId) {

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!principal.getId().equals(userId)) {
            throw new AccessDeniedException("Not allowed to create post for another user");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String safeContent = InputSanitizer.sanitizeHtml(req.getContent());
        String safeFeeling = InputSanitizer.sanitizeHtml(req.getFeeling());
        String safeCaption = InputSanitizer.sanitizeHtml(req.getCaption());
        String safeLocation = InputSanitizer.sanitizeHtml(req.getLocation());

        Post post = new Post();
        post.setAuthor(author);
        post.setContent(safeContent);
        post.setFeeling(safeFeeling);
        post.setCaption(safeCaption);
        post.setLocation(safeLocation);
        post.setLikeCount(0);

        Post saved = postRepository.save(post);


        if( author.getFriendsCount() > 0  ) {

            for (User friend : author.getFriends()) {

                notificationService.create(friend.getId(), friend.getUsername(), NotificationType.GENERAL,
                        "Someone has published a new post",
                        author.getUsername() + " has published a new post", author.getId(), saved.getId());

            }
        }
        return PostMapper.toDto(saved);
    }

    @Transactional
    public PostDTO updatePost(Long id, UpdatePostDTO req) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!post.getAuthor().getId().equals(principal.getId())) {
            throw new AccessDeniedException("Not allowed to update this post");
        }

        PostDTO postDTO = PostMapper.toDto(post);
        String safeContent = InputSanitizer.sanitizeHtml(req.getContent());
        postDTO.setContent(safeContent);
        String safeFeeling = InputSanitizer.sanitizeHtml(req.getFeeling());
        postDTO.setFeeling(safeFeeling);
        String safeCaption = InputSanitizer.sanitizeHtml(req.getCaption());
        postDTO.setCaption(safeCaption);
        String safeLocation = InputSanitizer.sanitizeHtml(req.getLocation());
        postDTO.setLocation(safeLocation);

        Post updatedPost = PostMapper.toEntity(postDTO, userRepository);

        return PostMapper.toDto(postRepository.saveAndFlush(updatedPost));
    }

    @Transactional
    public void deletePost(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!post.getAuthor().getId().equals(principal.getId())) {
            throw new AccessDeniedException("Not allowed to delete this post");
        }

        postRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<CommentDTO> getPostComments(Long postId, Pageable pageable) {

        postRepository.findById(postId)

                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return commentRepository.findByPostId(postId, pageable).map(CommentMapper::toDto);
    }

    @Transactional
    public CommentDTO createComment(Long postId, CommentDTO commentDTO, Long userId) {

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.getId().equals(userId)) {
            throw new AccessDeniedException("Not allowed to create comment for another user");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        User user = userRepository.getReferenceById(userId);

        String safeContent = InputSanitizer.sanitizeHtml(commentDTO.getContent());

        Comment comment = new Comment();
        comment.setContent(safeContent);
        comment.setPost(post);
        comment.setAuthor(user);

        Comment savedComment = commentRepository.saveAndFlush(comment);

        if( user.getFriendsCount() > 0  ) {

            for (User friend : user.getFriends()) {

                notificationService.create(friend.getId(), friend.getUsername(), NotificationType.GENERAL,
                        "Someone has published a new comment",
                        user.getUsername() + " has published a new comment", user.getId(), savedComment.getId());

            }
        }

        return CommentMapper.toDto(savedComment);
    }

    @Transactional
    public CommentDTO updateComment(Long postId, Long commentId, UpdateCommentDTO req) {

        String safeContent = InputSanitizer.sanitizeHtml(req.getContent());
        req.setContent(safeContent);

        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!comment.getAuthor().getId().equals(principal.getId())) {
            throw new AccessDeniedException("Not allowed to update this comment");
        }

        String newContent = req.getContent();
        if (newContent != null) {
            newContent = newContent.trim();
            if (!newContent.isEmpty() && !newContent.equals(comment.getContent())) {
                comment.setContent(newContent);
            }
        }

        commentRepository.saveAndFlush(comment);
        return CommentMapper.toDto(comment);
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!comment.getAuthor().getId().equals(principal.getId())) {
            throw new AccessDeniedException("Not allowed to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public void likePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.saveAndFlush(post);

    }

    @Transactional
    public void unlikePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.saveAndFlush(post);
        }
    }

    @Transactional(readOnly = true)
    public int getLikeCount(Long postId) {

        return postRepository.findById(postId)
                .map(Post::getLikeCount)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    @Transactional(readOnly = true)
    public List<User> getUsersWhoLiked(Long postId) {
        return postlikeRepository.findUsersWhoLikedPost(postId);
    }


}
