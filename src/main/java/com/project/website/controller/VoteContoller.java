package com.project.website.controller;

import com.project.website.dto.VoteDto;
import com.project.website.service.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/votes/")
public class VoteContoller {
	private final VoteService voteService;

	@PostMapping
	public ResponseEntity<Void> vote(@RequestBody VoteDto voteDto) {
		voteService.vote(voteDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
