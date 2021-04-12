package com.example.demo.repository;
import com.example.demo.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Integer> {

    Optional<Game> findByName(String name);

    @Modifying
    @Query(nativeQuery = true, value = "delete from game g where \n" +
            "not exists(select * from post_games where game_id = g.id) \n" +
            "and not exists(select * from unapproved_post_games where game_id = g.id)")
    void deleteAllUnusedGames();
}
