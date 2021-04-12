package com.example.demo.repository;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {

    Optional<Post> findByIdAndApprovedTrue(int id);

    @Query("select p from Post p where p.status" +
            "= com.example.demo.enumeration.Status.PUBLIC and p.approved=true")
    List<Post> findAllByStatusIsPublic();

    List<Post> findAllByApprovedIsTrue();

    List<Post> findAllByAuthor(User author);
}
