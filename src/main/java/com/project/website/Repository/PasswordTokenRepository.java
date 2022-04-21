package com.project.website.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.website.Model.PasswordToken;

@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Long> {

	Optional<PasswordToken> findByToken(String token);

}
