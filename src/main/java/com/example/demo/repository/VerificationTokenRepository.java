package com.example.demo.repository;
import com.example.demo.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import javax.transaction.Transactional;
import java.util.Optional;

public interface VerificationTokenRepository extends
        JpaRepository<VerificationToken,Integer> {

    Optional<VerificationToken> findFirstByToken(String token);

    @Transactional
    @Modifying
    @Query("delete from VerificationToken t where t.expiryDate < current_timestamp")
    void deleteByExpiryDateBeforeCurrent();
}
