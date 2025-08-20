package com.socialmedia.petTreff.mapper;

import com.socialmedia.petTreff.dto.CommentDTO;
import com.socialmedia.petTreff.entity.Comment;

public class CommentMapper {

    public static CommentDTO toDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setUserId(comment.getAuthor().getId());
        dto.setPostId(comment.getPost().getId());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        dto.setAuthorUsername(comment.getAuthor().getUsername());
        dto.setVersion(comment.getVersion());
        return dto;
    }

    public static Comment toEntity(CommentDTO dto) {
        if (dto == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setId(dto.getId());
        comment.setContent(dto.getContent());

        return comment;
    }

}
