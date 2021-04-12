package com.example.demo.repository;
import com.example.demo.entity.Comment;
import com.example.demo.entity.UnapprovedComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnapprovedCommentRepository extends
        JpaRepository<UnapprovedComment, Integer> {
      void deleteByComment(Comment comment);
}
