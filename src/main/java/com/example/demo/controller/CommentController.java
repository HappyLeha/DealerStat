package com.example.demo.controller;

import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.PostDTO;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.exception.NotEnoughRightException;
import com.example.demo.service.CommentService;
import com.example.demo.service.PostService;
import com.example.demo.service.UserService;
import com.example.demo.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class CommentController {
    private CommentService commentService;

    private PostService postService;

    private UserService userService;

    @Autowired
    public CommentController(CommentService commentService,
                             PostService postService,
                             UserService userService) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/posts/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public void createComment(@PathVariable("id") int id,
                              @RequestBody @Valid CommentDTO commentDTO,
                              Principal principal) {

        User user = userService.getUserByEmailAndEnabled(principal.getName());
        Post post = postService.getPost(id);
        Comment comment;

        if (user.equals(post.getAuthor())) {
            throw new NotEnoughRightException("You can't rate this post");
        }
        comment = Mapper.convertToComment(commentDTO,userService.isAdmin(user),
                                          user, post);
        commentService.createComment(comment, post, user);
    }

    @PostMapping("comments/{id}/approve")
    public void approveComment(@PathVariable("id") int id) {
        Comment comment = commentService.getUnapprovedComment(id);

        commentService.approveComment(comment);
    }

    @GetMapping("comments/{id}/unapproved")
    public CommentDTO getUnapprovedComment(@PathVariable("id") int id) {
        return Mapper.convertToCommentDTO(commentService.getUnapprovedComment(id));
    }

    @GetMapping("comments/{id}")
    public CommentDTO getComment(@PathVariable("id") int id) {
        return Mapper.convertToCommentDTO(commentService.getComment(id));
    }

    @GetMapping("posts/{id}/comments")
    public List<CommentDTO> getAllCommentsByPost(@PathVariable("id") int id) {
        Post post = postService.getPost(id);

        return Mapper.convertToListCommentDTO(commentService.getAllCommentsByPost(post));
    }

    @GetMapping("users/{id}/comments")
    public List<CommentDTO> getAllCommentsByAuthor(@PathVariable("id") int id) {
        User user = userService.getUser(id);

        return Mapper.convertToListCommentDTO(commentService.getAllCommentsByAuthor(user));
    }

    @PutMapping("comments/{id}")
    public void updateComment(@PathVariable("id") int id,
                           @RequestBody @Valid CommentDTO commentDTO,
                           Principal principal) {
        User user = userService.getUserByEmailAndEnabled(principal.getName());
        Comment comment = commentService.getUnapprovedComment(id);

        if (!user.equals(comment.getAuthor())) {
            throw new NotEnoughRightException("You can't rate this post");
        }

        commentService.updateComment(comment, commentDTO, userService.isAdmin(user));
    }

    @DeleteMapping("comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("id") int id, Principal principal) {
        Comment comment = commentService.getUnapprovedComment(id);
        User user = userService.getUserByEmailAndEnabled(principal.getName());

        if (!userService.isAdmin(user) && !comment.getAuthor().equals(user)) {
            throw new NotEnoughRightException("You can't delete this comment");
        }

        commentService.deleteComment(comment);
    }
}
