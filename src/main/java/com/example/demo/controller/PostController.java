package com.example.demo.controller;
import com.example.demo.dto.GameDTO;
import com.example.demo.dto.PostDTO;
import com.example.demo.entity.Game;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.enumeration.Status;
import com.example.demo.exception.NotEnoughRightException;
import com.example.demo.service.PostService;
import com.example.demo.service.UserService;
import com.example.demo.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("posts")
public class PostController {
    private PostService postService;
    private UserService userService;

    @Autowired
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(@RequestBody @Valid PostDTO postDTO,
                           Principal principal) {
        User user = userService.getUserByEmailAndEnabled(principal.getName());
        List<Game> games = postService.getGamesByGameDTOS(postDTO.getGames());
        Post post = Mapper.convertToPost(postDTO,
                userService.isAdmin(user), user, games);

        postService.createPost(post);
    }

    @PostMapping("/{id}/approve")
    public void approvePost(@PathVariable("id") int id) {
        Post post = postService.getUnapprovedPost(id);

        postService.approvePost(post);
    }

    @GetMapping("/{id}")
    public PostDTO getPost(@PathVariable("id") int id, Principal principal) {
        Post post = postService.getPost(id);

        if (post.getStatus().equals(Status.DRAFT) && principal == null) {
            throw new NotEnoughRightException("You can't browse this post!");
        }
        return Mapper.convertToPostDTO(post);
    }

    @GetMapping("/{id}/unapproved")
    public PostDTO getUnapprovedPost(@PathVariable("id") int id) {
        return Mapper.convertToPostDTO(postService.getUnapprovedPost(id));
    }

    @GetMapping
    public List<PostDTO> getAllPosts(Principal principal) {
        if (principal == null) {
            return Mapper.convertToListPostDTO(postService.getAllPublicPosts());
        }
        else {
            return Mapper.convertToListPostDTO(postService.getAllPosts());
        }
    }

    @GetMapping("/my")
    public List<PostDTO> getAllMyPosts(Principal principal) {
        User user = userService.getUserByEmailAndEnabled(principal.getName());

        return Mapper.convertToListPostDTO(postService.getAllMyPosts(user));
    }

    @GetMapping("/games")
    public List<GameDTO> getAllGames() {
        return Mapper.convertToListGameDTO(postService.getAllGames());
    }

    @PutMapping("/{id}")
    public void updatePost(@PathVariable("id") int id,
                           @RequestBody @Valid PostDTO postDTO,
                           Principal principal) {
        Post post = postService.getUnapprovedPost(id);
        User user = userService.getUserByEmailAndEnabled(principal.getName());

        if (!post.getAuthor().equals(user)) {
            throw new NotEnoughRightException("You can't change this post!");
        }
        postService.updatePost(post, postDTO, userService.isAdmin(user));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable("id") int id, Principal principal) {
        Post post = postService.getUnapprovedPost(id);
        User user = userService.getUserByEmailAndEnabled(principal.getName());

        if (!userService.isAdmin(user) && !post.getAuthor().equals(user)) {
            throw new NotEnoughRightException("You can't delete this post!");
        }
        postService.deletePost(post);
    }
}
