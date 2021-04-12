package com.example.demo.repository;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    boolean existsByRoleAndEnabledTrue(Role role);

    boolean existsByEmail(String email);

    @Query("select avg(c.rating) from User u, Post p, Comment c " +
            "where u = :user and p.author = u and c.post = p group by u")
    Double findRatingByUser(@Param("user") User user);

    Optional<User> findByEmailAndEnabledTrue(String email);

    Optional<User> findByIdAndEnabledTrue(int id);

    @Query("select u from User u where u.role.name = 'ROLE_READER'")
    List<User> findAllReaders();

    @Query("select u from User u where u.role.name <> 'ROLE_READER'")
    List<User> findAllNonReaders();

    @Query(nativeQuery = true, value = "select * from user u where u.id" +
            "in(select distinct p.author_id from post p where p.id \n" +
            "in(select distinct pg.posts_id from post_games pg" +
            "where pg.game_id in(:games)))")
    List<User> findUserByGames(@Param("games") List<Integer> games);

    @Transactional
    void deleteByTokenIsNullAndEnabledFalse();
}
