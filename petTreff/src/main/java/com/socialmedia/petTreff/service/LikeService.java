package com.socialmedia.petTreff.service;

import com.socialmedia.petTreff.dto.LikeDTO;
import com.socialmedia.petTreff.entity.NotificationType;
import com.socialmedia.petTreff.entity.Post;
import com.socialmedia.petTreff.entity.PostLike;
import com.socialmedia.petTreff.entity.User;
import com.socialmedia.petTreff.mapper.LikeMapper;
import com.socialmedia.petTreff.repository.LikeRepository;
import com.socialmedia.petTreff.repository.PostRepository;
import com.socialmedia.petTreff.repository.UserRepository;
import com.socialmedia.petTreff.security.UserPrincipal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostService postService;

    private final UserRepository userRepository;

    private PostRepository postRepository;

    private LikeRepository likeRepository;

    private final NotificationService notificationService;


    public List<PostLike> getAllLikes() {
        return likeRepository.findAll();
    }

    public Optional<PostLike> getLikeById(Long id) {
        return likeRepository.findById(id);
    }


    public LikeDTO createLike(Long postId, UserPrincipal userPrincipal) {

        Long userId = userPrincipal.getId();

        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new IllegalStateException("Already liked");
        }

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.getId().equals(userId)) {
            throw new AccessDeniedException("Not allowed to create like for another user");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        postService.likePost(postId);

        User user = userRepository.getReferenceById(userId);

        PostLike newLike = new PostLike();

        newLike.setUser(user);
        newLike.setPost(post);

        PostLike saved =likeRepository.save(newLike);

        User recipient = post.getAuthor();

        if(recipient.getId().equals(userId)) {   return LikeMapper.toDto(saved);  }


        notificationService.create(recipient.getId(), recipient.getUsername(), NotificationType.GENERAL,
                "Someone liked your post",
                user.getUsername() + "has liked your post " ,  user.getId() , saved.getId() );


        return LikeMapper.toDto(saved);
    }

    public void deleteLike(Long postId, Long userId) {

        PostLike like = likeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new IllegalArgumentException("Like not found"));

        likeRepository.delete(like);

        postService.unlikePost(postId);
    }
}
