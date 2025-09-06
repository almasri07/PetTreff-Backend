package com.socialmedia.petTreff.service;

import com.socialmedia.petTreff.dto.CommentDTO;
import com.socialmedia.petTreff.entity.Comment;
import com.socialmedia.petTreff.mapper.CommentMapper;
import com.socialmedia.petTreff.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {


    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public List<CommentDTO> getAllComments() {

        return commentRepository.findAll().stream().map(CommentMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CommentDTO getCommentById(Long id) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        return CommentMapper.toDto(comment);
    }

}
