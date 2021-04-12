package com.example.demo.service;
import com.example.demo.dto.CommentDTO;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import java.util.List;

public interface CommentService {
    void createComment(Comment comment, Post post, User user);
    void approveComment(Comment comment);
    Comment getComment(int id);
    Comment getUnapprovedComment(int id);
    List<Comment> getAllCommentsByPost(Post post);
    List<Comment> getAllCommentsByAuthor(User user);
    void updateComment(Comment comment, CommentDTO commentDTO, boolean admin);
    void deleteComment(Comment comment);
}
