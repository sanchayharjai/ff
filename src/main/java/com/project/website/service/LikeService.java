package com.project.website.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.project.website.Model.Likes;
import com.project.website.Model.Post;
import com.project.website.Repository.LikeRepository;
import com.project.website.Repository.PostRepository;
import com.project.website.dto.LikeDto;
import com.project.website.exceptions.RecipeException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LikeService {

	private final AuthService authService;
	private final PostRepository postRepository;
	private final LikeRepository likeRepository;

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void like(LikeDto likeDto) {
		
		Post post = postRepository.findById(likeDto.getPostId()).orElseThrow(() -> new RecipeException(""));
		Optional<Likes> like = likeRepository.findByPostAndUser(post, authService.getCurrentUser());
		if (like.isPresent()) {
			post.setLikeCount(post.getLikeCount() - 1);
			likeRepository.delete(like.orElseThrow(() -> new RecipeException("")));
		} else {
			post.setLikeCount(post.getLikeCount() + 1);
			likeRepository.save(mapToDto(post));
		}
		postRepository.save(post);
	}

	private Likes mapToDto(Post post) {
		return Likes.builder().post(post).user(authService.getCurrentUser()).build();

	}

}
