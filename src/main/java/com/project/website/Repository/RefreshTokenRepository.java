package com.project.website.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.website.Model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
Optional<RefreshToken> findByToken (String token);
void deleteByToken(String token);
}
