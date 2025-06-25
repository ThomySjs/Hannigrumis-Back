package com.Hannigrumis.api.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface RecoveryCodeRepository extends JpaRepository<RecoveryCode, Integer> {
    @Query("SELECT c FROM RecoveryCode c WHERE c.email = :email")
    RecoveryCode findByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM RecoveryCode c WHERE c.email = :email")
    int deleteTokenByEmail(@Param("email") String email);

}