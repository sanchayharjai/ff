package com.project.website.controller;

import static org.springframework.http.ResponseEntity.status;


import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.website.dto.AuthenticationResponse;
import com.project.website.dto.LoginRequest;
import com.project.website.dto.RefreshTokenRequest;
import com.project.website.dto.RegisterRequest;
import com.project.website.dto.tokenDto;
import com.project.website.service.AuthService;
import com.project.website.service.RefreshTokenService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;

	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest) {
		authService.signup(registerRequest);
		return new ResponseEntity<>("User registration successful", HttpStatus.OK);
	}

	@PostMapping("accountVerification/")
	public ResponseEntity<Void> verifyAccount(@RequestBody tokenDto token) {
		System.out.println(token.getToken());
		authService.verifyAccount(token.getToken());
		return new ResponseEntity<>(HttpStatus.OK);
	}


	@PostMapping("/login")
	public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
		return authService.login(loginRequest);
	}

	@PostMapping("/refresh/token")
	public AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
		return authService.refreshToken(refreshTokenRequest);
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
		refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
		return ResponseEntity.status(HttpStatus.OK).body("Refresh Token Deleted Successfully!!");
	}

	@GetMapping("/usernameverification")
	public ResponseEntity<Boolean> usernameVerification(@RequestParam String username) {
		return status(HttpStatus.OK).body(authService.usernameVerification(username));

	}

	@GetMapping("/emailverification")
	public ResponseEntity<Boolean> emailVerification(@RequestParam String email) {
		return status(HttpStatus.OK).body(authService.emailVerification(email));
	}

}
