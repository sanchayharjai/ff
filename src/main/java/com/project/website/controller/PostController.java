package com.project.website.controller;

import static org.springframework.http.ResponseEntity.status;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.project.website.dto.PostDto;
import com.project.website.service.PostService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
public class PostController {
	private final PostService postService;

	@PostMapping
	public ResponseEntity<Void> createPost(@RequestParam(required = false) String text,
			@RequestParam(required = false) String url, @RequestParam(required = false) MultipartFile[] images) {

		postService.save(text, url, images);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/")
	public ResponseEntity<Page<PostDto>> getAllPosts(@RequestParam int page, @RequestParam int elements) {
		return status(HttpStatus.OK).body(postService.getAllPost(page, elements));

	}

	@GetMapping("/{id}")
	public ResponseEntity<PostDto> getPost(@PathVariable Long id) {
		return status(HttpStatus.OK).body(postService.getPost(id));

	}

	@GetMapping("/by-userId/{userId}")
	public ResponseEntity<Page<PostDto>> getPostByUserId(@PathVariable Long userId, @RequestParam int page,
			@RequestParam int elements) {
		return status(HttpStatus.OK).body(postService.getPostByUserId(userId, page, elements));

	}

	@DeleteMapping("/delete/{postId}")
	public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
		postService.delete(postId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
