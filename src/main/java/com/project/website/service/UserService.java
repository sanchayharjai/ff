package com.project.website.service;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.project.website.Model.Followers;
import com.project.website.Model.NotificationEmail;
import com.project.website.Model.PasswordToken;
import com.project.website.Model.Recipe;
import com.project.website.Model.User;
import com.project.website.Repository.CommentRepository;
import com.project.website.Repository.FollowerRepository;
import com.project.website.Repository.PasswordTokenRepository;
import com.project.website.Repository.RecipeRepository;
import com.project.website.Repository.UserRepository;
import com.project.website.config.AppConfig;
import com.project.website.dto.DashDto;
import com.project.website.dto.FollowerDto;
import com.project.website.dto.ForgotPasswordDto;
import com.project.website.dto.RecipeDashDto;
import com.project.website.dto.UserDto;
import com.project.website.exceptions.RecipeException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final AuthService authService;
	private final PasswordEncoder passwordEncoder;
	private final PasswordTokenRepository passwordTokenRepository;
	private final MailService mailService;
	private final FollowerRepository followerRepository;
	private final RecipeService recipeService;
	private final AppConfig appConfig;
	private final SecureRandom rand = new SecureRandom();
	private final CommentRepository commentRepository;
	private final RecipeRepository recipeRepository;

	public UserDto mapEntityDto(User user) {
		return UserDto.builder().userName(user.getUsername()).about(user.getAbout()).name(user.getName())
				.followers(user.getFollowers())
				.profileImageL(user.getProfileImageL())
				.isFollowed(recipeService.isFollowed(user)).currUser(currUser(user)).userId(user.getUserId())
				.following(followerRepository.findByUser(user, PageRequest.of(0, 1)).getTotalElements())
				.balance(user.getBalance()).build();
	}

	@Transactional
	public UserDto getUser(Long userId) {
		return mapEntityDto(userRepository.findById(userId).orElseThrow(() -> new RecipeException("User Not Found")));
	}

	@Transactional
	public void changeUsername(String newUsername) {

		if (userRepository.findByUsername(newUsername).isPresent()) {
			throw new RecipeException("Username taken");
		} else {
			User currentUser = authService.getCurrentUser();
			currentUser.setUsername(newUsername);
			userRepository.save(currentUser);

		}

	}

	@Transactional
	public void change(String about) {
		User currentUser = authService.getCurrentUser();

		if (about != null) {
			currentUser.setAbout(about);
		}

		userRepository.save(currentUser);
	}

	@Transactional
	public void changePassword(String password, String oPassword) {
		User currentUser = authService.getCurrentUser();
		if (passwordEncoder.matches(oPassword, currentUser.getPassword())) {
			currentUser.setPassword(passwordEncoder.encode(password));
			userRepository.save(currentUser);
		} else {

			throw new RecipeException("Old Password doesnt match");

		}
	}

	@Transactional
	public void changeProfilePic(MultipartFile profileImage) {
		User currentUser = authService.getCurrentUser();

		currentUser.setProfileImageL(recipeService.saveImage(profileImage, 200, 200));
		currentUser.setProfileImageS(recipeService.saveImage(profileImage, 60, 60));

		userRepository.save(currentUser);
	}

	@Transactional
	public void changeAbout(String about) {
		User currentUser = authService.getCurrentUser();
		currentUser.setAbout(about);
		userRepository.save(currentUser);
	}

	public String generateVerificationToken(String email, String password) {
		if (!userRepository.findByEmail(email).isPresent()) {
			throw new RecipeException("User associated with the following email does not exist");
		}
		String token = String.valueOf(rand.nextLong() & Long.MAX_VALUE);
		PasswordToken passwordToken = new PasswordToken();
		passwordToken.setToken(token);
		passwordToken.setEmail(email);
		passwordToken.setPassword(passwordEncoder.encode(password));
		passwordTokenRepository.save(passwordToken);

		return token;
	}

	public void forgotPassword(ForgotPasswordDto userDetails) {
		String token = generateVerificationToken(userDetails.getEmail(), userDetails.getNPassword());
		mailService.sendMail(userDetails.getEmail(), "Please click on the following button to verify password change",
				"Please verify your email", appConfig.getUrl() + "passwordReset/" + token);

	}

	public void verifyPasswordChange(String token) {
		Optional<PasswordToken> passwordToken = passwordTokenRepository.findByToken(token);
		passwordToken.orElseThrow(() -> new RecipeException("Invalid Token"));
		fetchUserAndChange(passwordToken.get());
	}

	@Transactional
	private void fetchUserAndChange(PasswordToken passwordToken) {
		String email = passwordToken.getEmail();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RecipeException("User not found"));
		user.setPassword(passwordToken.getPassword());
		passwordTokenRepository.delete(passwordToken);
		userRepository.save(user);
	}

	@Transactional
	public Page<FollowerDto> getFollowers(Long userId, int page, int elements) {
		return followerRepository.findByFollowedUserId(userId, PageRequest.of(page, elements)).map(this::mapEntityDto);
	}

	@Transactional
	public Page<FollowerDto> getFollowing(Long userId, int page, int elements) {
		return followerRepository.findByUser(userRepository.findById(userId).orElseThrow(() -> new RecipeException("")),
				PageRequest.of(page, elements)).map(this::mapEntityDtoFollowing);
	}

	@Transactional
	private FollowerDto mapEntityDto(Followers followers) {
		User follower = followers.getUser();

		return FollowerDto.builder().userId(follower.getUserId()).username(follower.getUsername())
				.isFollowed(recipeService.isFollowed(follower))
				.currUser(authService.getCurrentUser().getUserId() == follower.getUserId())
				.profileI(follower.getProfileImageL()).build();

	}

	@Transactional
	private FollowerDto mapEntityDtoFollowing(Followers followers) {
		User follower = userRepository.findById(followers.getFollowedUserId())
				.orElseThrow(() -> new RecipeException(""));

		return FollowerDto.builder().userId(follower.getUserId()).username(follower.getUsername())
				.isFollowed(recipeService.isFollowed(follower))
				.currUser(authService.getCurrentUser().getUserId() == follower.getUserId())
				.profileI(follower.getProfileImageL()).build();

	}

	private boolean currUser(User user) {
		if (authService.isLoggedIn()) {
			return authService.getCurrentUser().getUserId() == user.getUserId();
		}
		return false;
	}

	@Transactional
	public void withdraw(String details, Long userId) {
		Optional<User> user = userRepository.findById(userId);
		if (user.isPresent()) {
			user.get().setWBal((int) (user.get().getWBal()));
			user.get().setDetails(details);
			userRepository.save(user.get());
		}

	}

	@Transactional
	public DashDto dash(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RecipeException(""));
		return DashDto.builder().username(user.getUsername()).balance(user.getBalance()).pImage(user.getProfileImageL())
				.comments(commentRepository.findAllByUser(user, PageRequest.of(0, 1)).getTotalElements())
				.followers(user.getFollowers())
				.recipes(recipeRepository.findByUserAndApproved(user, true, PageRequest.of(0, 1)).getTotalElements())
				.tEarnings(user.getTEarnings()).tViews(user.getTViews()).about(user.getAbout()).build();
	}

	@Transactional
	public Page<RecipeDashDto> recipeDash(Long userId, int page, int elements) {

		return recipeRepository
				.findByUserAndApproved(userRepository.findById(userId).orElseThrow(() -> new RecipeException("")), true,
						PageRequest.of(page, elements, Direction.DESC, "createdDate"))
				.map(this::mapDashRecipe);
	}

	public RecipeDashDto mapDashRecipe(Recipe recipe) {
		return RecipeDashDto.builder().recipeName(recipe.getRecipeName())
				.commentCount(commentRepository.findByRecipe(recipe, PageRequest.of(0, 1)).getTotalElements())
				.createdDate(RecipeService.getDuration(recipe.getCreatedDate())).thumbnail(recipe.getThumbnailS())
				.views(recipe.getViews()).voteCount(recipe.getVoteCount()).build();
	}

}
