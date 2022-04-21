package com.project.website.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.website.dto.CommentsDto;
import com.project.website.service.CommentService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/comment")
@AllArgsConstructor
public class CommentsController {

	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<Void> createComment(@RequestBody CommentsDto commentsDto) {
		commentService.save(commentsDto);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/by-recipe/{recipeId}")
	public ResponseEntity<Page<CommentsDto>> getAllCommentsForRecipe(@PathVariable Long recipeId,
			@RequestParam int page, @RequestParam int elements) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(commentService.getAllCommentsForRecipe(recipeId, page, elements));

	}

	@GetMapping("/by-post/{postId}")
	public ResponseEntity<Page<CommentsDto>> getAllCommentsForPost(@PathVariable Long postId, @RequestParam int page,
			@RequestParam int elements) {
		return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForPost(postId, page, elements));

	}

	@GetMapping("/by-user/{userName}")
	public ResponseEntity<Page<CommentsDto>> getAllCommentsForUser(@PathVariable String userName,
			@RequestParam int page) {
		return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForUser(userName, page));
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Void> delete(@RequestParam Long commentId) {
		commentService.delete(commentId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
