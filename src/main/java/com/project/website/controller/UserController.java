package com.project.website.controller;

import static org.springframework.http.ResponseEntity.status;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.website.dto.DashDto;
import com.project.website.dto.FollowerDto;
import com.project.website.dto.ForgotPasswordDto;
import com.project.website.dto.RecipeDashDto;
import com.project.website.dto.UserDto;
import com.project.website.dto.WithdrawDto;
import com.project.website.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/profile")
	public ResponseEntity<UserDto> getUser(@RequestParam Long userId) {
		return status(HttpStatus.OK).body(userService.getUser(userId));

	}

	@PutMapping("/change")
	public ResponseEntity<Void> change(@RequestParam String about) {
		userService.change(about);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/change/username")
	public ResponseEntity<Void> changeUsername(@RequestParam String newUsername) {
		userService.changeUsername(newUsername);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/change/password")
	public ResponseEntity<Void> changePassword(@RequestParam String oPassword,@RequestParam String nPassword) {
		userService.changePassword(nPassword,oPassword);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/change/profileimage")
	public ResponseEntity<Void> changeProfileImage(@RequestParam MultipartFile profileImage) {
		
		userService.changeProfilePic(profileImage);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/change/about")
	public ResponseEntity<Void> changeAbout(@RequestParam String about) {
		userService.changeAbout(about);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/forgotpassword")
	public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordDto newDetails) {
	
		userService.forgotPassword(newDetails);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/passwordVerification/{token}")
	public ResponseEntity<Void> verifyPasswordChange(@PathVariable String token) {
		userService.verifyPasswordChange(token);
		return  new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/followedby")
	public ResponseEntity<Page<FollowerDto>> getFollowers(@RequestParam Long userId, @RequestParam int page,
			@RequestParam int elements) {
		return status(HttpStatus.OK).body(userService.getFollowers(userId, page, elements));

	}
	@GetMapping("/following")
	public ResponseEntity<Page<FollowerDto>> getFollowing(@RequestParam Long userId, @RequestParam int page,
			@RequestParam int elements) {
		return status(HttpStatus.OK).body(userService.getFollowing(userId, page, elements));

	}
	@PostMapping("/withdraw")
	public ResponseEntity<Void> withdraw(@RequestBody WithdrawDto withdrawDto) {
		userService.withdraw(withdrawDto.getDetails(),withdrawDto.getUserId());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@GetMapping("/dash")
	public ResponseEntity<DashDto> dash(@RequestParam Long userId) {
		return status(HttpStatus.OK).body(userService.dash(userId));

	}
	@GetMapping("/recipedash")
	public ResponseEntity<Page<RecipeDashDto>> recipeDash(@RequestParam Long userId, @RequestParam int page, @RequestParam int elements) {
		return status(HttpStatus.OK).body(userService.recipeDash(userId,page,elements));

	}

}
