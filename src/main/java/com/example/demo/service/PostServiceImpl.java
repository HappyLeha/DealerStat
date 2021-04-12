package com.example.demo.service;
import com.example.demo.dto.GameDTO;
import com.example.demo.dto.PostDTO;
import com.example.demo.entity.*;
import com.example.demo.enumeration.Status;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UnapprovedPostRepository;
import com.example.demo.util.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UnapprovedPostRepository unapprovedPostRepository;
    private final GameRepository gameRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository,
                           UnapprovedPostRepository unapprovedPostRepository,
                           GameRepository gameRepository) {
        this.postRepository = postRepository;
        this.unapprovedPostRepository = unapprovedPostRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    public void createPost(Post post) {
        postRepository.save(post);
        log.info("Post " + post + " has been created.");
    }

    @Override
    public void approvePost(Post post) {
        post.setApproved(true);
        if (post.getUnapprovedPost() != null){
            unapprovedPostRepository.delete(post.getUnapprovedPost());
        }
        postRepository.save(post);
        log.info("Post " + post + " has been successfully approved.");
    }

    @Override
    @Transactional(readOnly = true)
    public Post getPost(int id) {
        Optional<Post> optionalPost = postRepository.findByIdAndApprovedTrue(id);

        if (!optionalPost.isPresent()) {
            log.info("Post with " + id + " doesn't exist!");
            throw new ResourceNotFoundException("This post doesn't exist!");
        }
        return optionalPost.get();
    }

    @Override
    @Transactional(readOnly = true)
    public Post getUnapprovedPost(int id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        Post post;

        if (!optionalPost.isPresent()) {
            log.info("Post with " + id + " doesn't exist!");
            throw new ResourceNotFoundException("This post doesn't exist!");
        }
        post = optionalPost.get();
        if (post.getUnapprovedPost() != null) {
            UnapprovedPost unapprovedPost = post.getUnapprovedPost();
            List<Game> games;

            if (unapprovedPost.getGames() != null) {
                games = new ArrayList<>(unapprovedPost.getGames());
            }
            else {
                games = null;
            }
            post.setTitle(unapprovedPost.getTitle());
            post.setText(unapprovedPost.getText());
            post.setStatus(unapprovedPost.getStatus());
            post.setGames(games);
        }
        return post;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getAllMyPosts(User user) {
        return postRepository.findAllByAuthor(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        return postRepository.findAllByApprovedIsTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getAllPublicPosts() {
        return postRepository.findAllByStatusIsPublic();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Game> getGamesByGameDTOS(List<GameDTO> gameDTOS) {
        List<Game> games = new ArrayList<>();

        if (gameDTOS == null) return games;
        gameDTOS = gameDTOS.stream().distinct().collect(Collectors.toList());
        for (GameDTO gameDTO: gameDTOS) {
            Optional<Game> optionalGame = gameRepository.findByName(gameDTO.getName());
            Game game;

            if (optionalGame.isPresent()) {
                game = optionalGame.get();
            } else {
                game = new Game(gameDTO.getName());
                gameRepository.save(game);
                log.info("Game " + game + " has been created.");
            }
            games.add(game);
        }
        return games;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getGameIdByName(String[] names) {
        List<Integer> idList = new ArrayList<>();
        boolean nonExist = false;

        for (String name: names) {
            Optional<Game> optionalGame = gameRepository.findByName(name);

            if (optionalGame.isPresent() && optionalGame.get().getPosts() != null) {
                idList.add(optionalGame.get().getId());
            } else {
                nonExist = true;
            }
        }
        if (idList.isEmpty() && nonExist) {
            return null;
        } else {
            return idList;
        }
    }

    @Override
    public void updatePost(Post post, PostDTO postDTO, boolean admin) {
        List<Game> games = getGamesByGameDTOS(postDTO.getGames());

        if (admin || !post.isApproved()) {
            post.setTitle(postDTO.getTitle());
            post.setText(postDTO.getText());
            post.setStatus(Status.valueOf(postDTO.getStatus()));
            post.setGames(games);
        }
        else {
            UnapprovedPost unapprovedPost = Mapper
                    .convertToUnapprovedPost(postDTO, games, post);

            unapprovedPostRepository.deleteByPost(post);
            unapprovedPostRepository.save(unapprovedPost);
        }
        postRepository.save(post);
        log.info("Post " + post + " has been updated.");
    }

    @Override
    public void deletePost(Post post) {
        postRepository.delete(post);
        log.info("Post " + post + " has been deleted.");
    }

    @Scheduled(cron = "0 0/15 * * * *")
    public void deleteUnusedGames() {
        gameRepository.deleteAllUnusedGames();
        log.info("Unused games have been deleted.");
    }
}
