package com.socialmedia.petTreff.service;

import com.socialmedia.petTreff.dto.CommentDTO;
import com.socialmedia.petTreff.entity.Comment;
import com.socialmedia.petTreff.mapper.CommentMapper;
import com.socialmedia.petTreff.repository.CommentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

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
    /*
     * @Transactional
     * public Comment createComment(Comment comment) {
     * Comment savedComment = commentRepository.save(comment);
     * return commentRepository.save(savedComment);
     * }
     * 
     * public Comment updateComment(Long id, Comment comment) {
     * comment.setId(id);
     * return commentRepository.save(comment);
     * }
     * 
     * public void deleteComment(Long id) {
     * commentRepository.deleteById(id);
     * }
     */
}
