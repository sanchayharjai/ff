package com.project.website.controller;

import com.project.website.dto.FollowerDto;
import com.project.website.service.FollowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/followers/")
public class FollowersController {
	private final FollowService followService;

	@PostMapping
	public ResponseEntity<Void> follow(@RequestBody FollowerDto followerDto) {
		followService.follow(followerDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
