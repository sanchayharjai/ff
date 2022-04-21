package com.project.website.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.project.website.exceptions.RecipeException;
import com.project.website.security.JwtProvider;
import com.project.website.Model.NotificationEmail;
import com.project.website.Model.User;
import com.project.website.Model.VerificationToken;
import com.project.website.Repository.UserRepository;
import com.project.website.Repository.VerificationTokenRepository;
import com.project.website.config.AppConfig;
import com.project.website.dto.AuthenticationResponse;
import com.project.website.dto.LoginRequest;
import com.project.website.dto.RefreshTokenRequest;
import com.project.website.dto.RegisterRequest;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final VerificationTokenRepository verificationTokenRepository;
	private final MailService mailService;
	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;
	private final RefreshTokenService refreshTokenService;
	private final AppConfig appConfig;
	private final SecureRandom rand = new SecureRandom();

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void signup(RegisterRequest registerRequest) {
		if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
			throw new RecipeException("Username taken");
		}
		if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
			throw new RecipeException("Email already registered");
		}

		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setCreated(Instant.now());
		user.setEnabled(true);
		user.setFollowers(Long.valueOf(0));
		user.setBalance(Float.valueOf(0));
		user.setTEarnings(Float.valueOf(0));
		user.setTViews(Long.valueOf(0));
		user.setWBal(Integer.valueOf(0));
		user.setRewards(false);

		userRepository.save(user);

		String token = generateVerificationToken(user);
		mailService.sendMail(user.getEmail(), "Please click on the following button to complete registration",
				"Account verification", appConfig.getUrl() + "accountVerification/" + token);

	}

	public String generateVerificationToken(User user) {
		String token = String.valueOf(rand.nextLong() & Long.MAX_VALUE);
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		verificationTokenRepository.save(verificationToken);

		return token;
	}

	public void verifyAccount(String token) {
		Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
		verificationToken.orElseThrow(() -> new RecipeException("Invalid Token"));
		fetchUserAndEnable(verificationToken.get());
	}

	@Transactional
	private void fetchUserAndEnable(VerificationToken verificationToken) {
		String username = verificationToken.getUser().getUsername();
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RecipeException("User not found"));
		user.setEnabled(true);
		verificationTokenRepository.delete(verificationToken);
		userRepository.save(user);

	}

	public AuthenticationResponse login(LoginRequest loginRequest) {

		Authentication authenticate = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authenticate);
		String token = jwtProvider.generateToken(authenticate);
		Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
		return AuthenticationResponse.builder().authenticationToken(token)
				.refreshToken(refreshTokenService.generateRefreshToken().getToken())
				.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
				.username(loginRequest.getUsername()).userId(getUserId(user)).profilePic(getProfileI(user))
				
				.build();

	}

	private Long getUserId(Optional<User> user) {
		if (user.isPresent()) {
			return user.get().getUserId();
		}

		return Long.valueOf(-1);
	}


	private String getProfileI(Optional<User> user) {
		if (user.isPresent() && user.get().getProfileImageL() != "" ) {
			return user.get().getProfileImageL();
		}
		return null;
	}

	@Transactional(readOnly = true)
	public User getCurrentUser() {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		
		return userRepository.findByUsername(principal.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
	}

	public AuthenticationResponse refreshToken(@Valid RefreshTokenRequest refreshTokenRequest) {

		refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
		String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
		return AuthenticationResponse.builder().authenticationToken(token)
				.refreshToken(refreshTokenRequest.getRefreshToken())
				.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
				.username(refreshTokenRequest.getUsername()).build();

	}

	public boolean isLoggedIn() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
	}

	public boolean usernameVerification(String username) {
		return userRepository.findByUsername(username).isPresent();
	}

	public boolean emailVerification(String email) {
		return userRepository.findByEmail(email).isPresent();
	}

}
