package com.project.website.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.website.Model.RefreshToken;
import com.project.website.Repository.RefreshTokenRepository;
import com.project.website.exceptions.RecipeException;

import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {
private final RefreshTokenRepository  refreshTokenRepository;
	public RefreshToken generateRefreshToken() {
		RefreshToken refreshToken= new RefreshToken();
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setCreatedDate(Instant.now());
		return refreshTokenRepository.save(refreshToken);
		
	}
	public void validateRefreshToken(String token) {
		refreshTokenRepository.findByToken(token).orElseThrow(()-> new RecipeException("Invalid refresh token"));
	}
	public void deleteRefreshToken(String token) {
		refreshTokenRepository.deleteByToken(token);
	}
	
}
