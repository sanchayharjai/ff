package com.project.website.service;

import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.website.dto.CommentsDto;
import com.project.website.exceptions.RecipeException;
import com.project.website.Model.Comment;
import com.project.website.Model.Post;
import com.project.website.Model.Recipe;
import com.project.website.Model.User;
import com.project.website.Repository.CommentRepository;
import com.project.website.Repository.PostRepository;
import com.project.website.Repository.RecipeRepository;
import com.project.website.Repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {
	private final RecipeRepository recipeRepository;
	private final UserRepository userRepository;
	private final AuthService authService;
	private final CommentRepository commentRepository;
	private final PostRepository postRepository;

	@Transactional
	public void save(CommentsDto commentsDto) {
		if (commentsDto.getRecipeId() == null) {
			Post post = postRepository.findById(commentsDto.getPostId())
					.orElseThrow(() -> new RecipeException("post not found"));
			Comment comment = map(commentsDto, post, authService.getCurrentUser());
			commentRepository.save(comment);
		} else {
			Recipe recipe = recipeRepository.findById(commentsDto.getRecipeId())
					.orElseThrow(() -> new RecipeException("recipe not found"));
			Comment comment = map(commentsDto, recipe, authService.getCurrentUser());
			commentRepository.save(comment);
		}
	}

	public Comment map(CommentsDto commentsDto, Recipe recipe, User user) {
		return Comment.builder().text(commentsDto.getText()).createdDate(Instant.now()).user(user).recipe(recipe)
				.build();

	}

	public Comment map(CommentsDto commentsDto, Post post, User user) {
		return Comment.builder().text(commentsDto.getText()).createdDate(Instant.now()).user(user).post(post).build();

	}

	public CommentsDto entityMapToDto(Comment comment) {
		return CommentsDto.builder().recipeId(getRecipeId(comment.getRecipe()))
				.username(comment.getUser().getUsername())
				.text(comment.getText()).userId(comment.getUser().getUserId())
				.duration(RecipeService.getDuration(comment.getCreatedDate())).postId(getPostId(comment.getPost()))
				.id(comment.getId()).byCurrUser(byCurrUser(comment)).profileI(comment.getUser().getProfileImageS())
				.build();
	}

	private boolean byCurrUser(Comment comment) {
		if (authService.isLoggedIn()) {
			return authService.getCurrentUser().getUserId() == comment.getUser().getUserId();
		}
		return false;
	}

	private Long getRecipeId(Recipe recipe) {
		if (recipe != null) {
			return recipe.getRecipeId();
		}
		return null;
	}

	private Long getPostId(Post post) {
		if (post != null) {
			return post.getPostId();
		}
		return null;
	}

	@Transactional(readOnly = true)
	public Page<CommentsDto> getAllCommentsForRecipe(Long recipeId, int page, int elements) {
		Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeException("recipe not found"));
		return commentRepository.findByRecipe(recipe, PageRequest.of(page, elements, Direction.DESC, "id"))
				.map(this::entityMapToDto);

	}

	@Transactional(readOnly = true)
	public Page<CommentsDto> getAllCommentsForPost(Long postId, int page, int elements) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new RecipeException("post not found"));
		return commentRepository.findByPost(post, PageRequest.of(page, elements, Direction.DESC, "id"))
				.map(this::entityMapToDto);

	}

	@Transactional(readOnly = true)
	public Page<CommentsDto> getAllCommentsForUser(String userName, int page) {
		User user = userRepository.findByUsername(userName).orElseThrow(() -> new RecipeException(""));
		return commentRepository.findAllByUser(user, PageRequest.of(page, 2)).map(this::entityMapToDto);

	}

	@Transactional
	public void delete(Long commentId) {
		if (authService.isLoggedIn()) {
			User user = authService.getCurrentUser();
			Comment comment = commentRepository.findById(commentId)
					.orElseThrow(() -> new RecipeException("Comment not found"));
			if (comment.getUser().getUserId() != user.getUserId()) {
				throw new RecipeException("comment not by curr user");
			}
			commentRepository.delete(comment);

		}
	}

}
