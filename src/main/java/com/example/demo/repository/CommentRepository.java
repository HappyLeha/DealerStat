package com.example.demo.repository;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    boolean existsByAuthorAndPost(User author, Post post);
    Optional<Comment> findByIdAndApprovedTrue(int id);
    List<Comment> findAllByPost(Post post);
    List<Comment> findAllByAuthor(User author);
}
