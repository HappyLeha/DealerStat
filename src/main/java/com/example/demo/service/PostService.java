package com.example.demo.service;
import com.example.demo.dto.GameDTO;
import com.example.demo.dto.PostDTO;
import com.example.demo.entity.*;
import java.util.List;

public interface PostService {
    void createPost(Post post);
    void approvePost(Post post);
    Post getPost(int id);
    Post getUnapprovedPost(int id);
    List<Post> getAllMyPosts(User user);
    List<Post> getAllPosts();
    List<Post> getAllPublicPosts();
    List<Game> getAllGames();
    List<Game> getGamesByGameDTOS(List<GameDTO> gameDTOS);
    List<Integer> getGameIdByName(String[] names);
    void updatePost(Post post, PostDTO postDTO, boolean admin);
    void deletePost(Post post);
}
