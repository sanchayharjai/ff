package com.project.website.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.website.dto.LikeDto;
import com.project.website.service.LikeService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/likes/")
public class LikesController {
	private final LikeService likeService;
	
	@PostMapping
	public ResponseEntity<Void> like(@RequestBody LikeDto likeDto) {
		likeService.like(likeDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
