package com.project.website.service;

import com.project.website.Model.Followers;
import com.project.website.Model.User;
import com.project.website.Repository.FollowerRepository;
import com.project.website.Repository.UserRepository;
import com.project.website.dto.FollowerDto;
import com.project.website.exceptions.RecipeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FollowService {
	private final AuthService authService;
	private final FollowerRepository followerRepository;
	private final UserRepository userRepository;

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void follow(FollowerDto followerDto) {
		System.out.println("follower");
		System.out.println(authService.getCurrentUser().getUserId());
		System.out.println("tobefollowed");
		System.out.println(followerDto.getUserId());
		Optional<Followers> follow = followerRepository.findByUserAndFollowedUserId(authService.getCurrentUser(),followerDto.getUserId());
		User user = userRepository.findById(followerDto.getUserId())
				.orElseThrow(() -> new RecipeException("User not found"));
		if (follow.isPresent()) {

			followerRepository.delete(follow.orElseThrow(() -> new RecipeException("")));
			user.setFollowers(user.getFollowers() - 1);
		} else {

			user.setFollowers(user.getFollowers() + 1);
			followerRepository.save(mapToFollow(authService.getCurrentUser(), followerDto.getUserId()));

		}
		userRepository.save(user);
	}

	private Followers mapToFollow(User user, Long followedUserId) {
		return Followers.builder().user(user).followedUserId(followedUserId).build();
	}
}
