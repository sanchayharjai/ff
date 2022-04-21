package com.project.website.controller;

import com.project.website.dto.RateDto;
import com.project.website.service.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/rate/")
public class RatingController {
	private final RatingService ratingService;

	@PostMapping
	public ResponseEntity<Void> rate(@RequestBody RateDto rateDto) {
		ratingService.rate(rateDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
