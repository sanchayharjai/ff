package com.project.website.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.website.dto.FavouriteDto;
import com.project.website.service.FavouriteService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/favourite")
public class FavouriteController {
	private final FavouriteService favouriteService;

	@PostMapping
	public ResponseEntity<Void> addToFavourite(@RequestBody FavouriteDto favouriteDto) {
		favouriteService.addToFavourite(favouriteDto);
		return new ResponseEntity<>(HttpStatus.OK);

	}
}