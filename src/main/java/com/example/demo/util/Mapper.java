package com.example.demo.util;
import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.GameDTO;
import com.example.demo.dto.PostDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.*;
import com.example.demo.enumeration.Status;
import java.util.ArrayList;
import java.util.List;

public class Mapper {

    public static User convertToUser(UserDTO userDTO, Role role) {
        return new User(userDTO.getFirstName(), userDTO.getLastName(),
                        userDTO.getPassword(), userDTO.getEmail(), role);
    }

    public static UserDTO convertToUserDTO(User user) {
        return new UserDTO().builder().id(user.getId()).firstName(user.getFirstName())
                .lastName(user.getLastName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .role(user.getRole().getName()).rating(user.getRating()).build();
    }

    public static List<UserDTO> convertToListUserDTO(List<User> users) {
        List<UserDTO> userDTOS = new ArrayList<>();

        for (User user: users) {
            userDTOS.add(Mapper.convertToUserDTO(user));
        }
        return userDTOS;
    }

    public static Post convertToPost(PostDTO postDTO, boolean approved,
                                     User user, List<Game> games) {
        return new Post().builder().title(postDTO.getTitle())
                .text(postDTO.getText()).status(Status.valueOf(postDTO.getStatus()))
                .approved(approved).user(user).games(games).build();
    }

    public static UnapprovedPost convertToUnapprovedPost(PostDTO postDTO,
                                                         List<Game> games, Post post) {
        return new UnapprovedPost(postDTO.getTitle(), postDTO.getText(),
                Status.valueOf(postDTO.getStatus()), games, post);
    }

    public static PostDTO convertToPostDTO(Post post) {
        List<GameDTO> gameDTOS = convertToListGameDTO(post.getGames());

        return new PostDTO().builder().id(post.getId())
                .title(post.getTitle()).text(post.getText())
                .status(post.getStatus().name())
                .authorId(post.getAuthor().getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .games(gameDTOS).build();
    }

    public static List<GameDTO> convertToListGameDTO(List<Game> games) {
        List<GameDTO> gameDTOS = new ArrayList<>();

        games.forEach(game ->
                    gameDTOS.add(new GameDTO(game.getId(), game.getName())));
        return gameDTOS;
    }

    public static List<PostDTO> convertToListPostDTO(List<Post> posts) {
        List<PostDTO> postDTOS = new ArrayList<>();

        for (Post post: posts) {
            postDTOS.add(convertToPostDTO(post));
        }
        return postDTOS;
    }

    public static Comment convertToComment(CommentDTO commentDTO, boolean approved,
                                           User user, Post post) {
        return new Comment(commentDTO.getMessage(), commentDTO.getRating(),
                           approved, post, user);
    }

    public static CommentDTO convertToCommentDTO(Comment comment) {
        return new CommentDTO().builder().id(comment.getId())
                .message(comment.getMessage()).rating(comment.getRating())
                .createdAt(comment.getCreatedAt()).updatedAt(comment.getUpdatedAt())
                .authorId(comment.getAuthor().getId())
                .postId(comment.getPost().getId()).build();
    }

    public static List<CommentDTO> convertToListCommentDTO(List<Comment> comments) {
        List<CommentDTO> commentDTOS = new ArrayList<>();

        for (Comment comment: comments) {
            commentDTOS.add(convertToCommentDTO(comment));
        }
        return commentDTOS;
    }

    public static UnapprovedComment convertToUnapprovedComment(CommentDTO commentDTO,
                                                               Comment comment) {
        return new UnapprovedComment(commentDTO.getMessage(), commentDTO.getRating(),
                comment);
    }
}
