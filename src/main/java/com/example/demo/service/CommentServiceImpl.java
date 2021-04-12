package com.example.demo.service;
import com.example.demo.dto.CommentDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceAlreadyExistException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import com.example.demo.util.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UnapprovedCommentRepository unapprovedCommentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              UnapprovedCommentRepository unapprovedCommentRepository) {
        this.commentRepository = commentRepository;
        this.unapprovedCommentRepository = unapprovedCommentRepository;
    }

    @Override
    public void createComment(Comment comment, Post post, User user) {
        if (commentRepository.existsByAuthorAndPost(user, post)) {
            log.info("Comment " + comment + " already exist!");
            throw new ResourceAlreadyExistException(
                    "Comment with this author and post already exist!");
        }
        commentRepository.save(comment);
        log.info("Comment " + comment + " has been created.");
    }

    @Override
    public void approveComment(Comment comment) {
        comment.setApproved(true);
        if (comment.getUnapprovedComment() != null){
            unapprovedCommentRepository.delete(comment.getUnapprovedComment());
        }
        log.info("Comment " + comment + " has been successfully approved.");
        commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getComment(int id) {
        Optional<Comment> optionalComment = commentRepository
                .findByIdAndApprovedTrue(id);
        Comment comment;

        if (!optionalComment.isPresent()) {
            log.info("Comment with  id " + id + " doesn't exist!");
            throw new ResourceNotFoundException("This comment doesn't exist!");
        }
        comment = optionalComment.get();
        return comment;
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getUnapprovedComment(int id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        Comment comment;

        if (!optionalComment.isPresent()) {
            log.info("Comment with  id " + id + " doesn't exist!");
            throw new ResourceNotFoundException("This comment doesn't exist!");
        }
        comment = optionalComment.get();
        if (comment.getUnapprovedComment() != null) {
            UnapprovedComment unapprovedComment = comment.getUnapprovedComment();

            comment.setMessage(unapprovedComment.getMessage());
            comment.setRating(unapprovedComment.getRating());
        }
        return comment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAllCommentsByPost(Post post) {
        return commentRepository.findAllByPost(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAllCommentsByAuthor(User user) {
        return commentRepository.findAllByAuthor(user);
    }

    @Override
    public void updateComment(Comment comment, CommentDTO commentDTO,
                              boolean admin) {
        if (admin || !comment.isApproved()) {
            comment.setMessage(commentDTO.getMessage());
            comment.setRating(commentDTO.getRating());
        } else {
            UnapprovedComment unapprovedComment = Mapper
                    .convertToUnapprovedComment(commentDTO, comment);

            unapprovedCommentRepository.deleteByComment(comment);
            unapprovedCommentRepository.save(unapprovedComment);
        }
        log.info("Comment " + comment + " has been updated.");
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Comment comment) {
        log.info("Comment " + comment + " has been deleted.");
        commentRepository.delete(comment);
    }
}
