package com.example.demo.repository;
import com.example.demo.entity.ResetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import javax.transaction.Transactional;
import java.util.Optional;

public interface ResetCodeRepository extends JpaRepository<ResetCode, Integer> {

    Optional<ResetCode> findByCode(String code);

    @Transactional
    @Modifying
    @Query("delete from ResetCode c where c.expiryDate < current_timestamp")
    void deleteByExpiryDateBeforeCurrent();
}
