package com.example.demo.repository;
import com.example.demo.entity.Post;
import com.example.demo.entity.UnapprovedPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnapprovedPostRepository extends
        JpaRepository<UnapprovedPost,Integer> {
    void deleteByPost(Post post);
}
