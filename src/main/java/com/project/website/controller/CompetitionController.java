package com.project.website.controller;

import static org.springframework.http.ResponseEntity.status;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.project.website.dto.CompetitionDto;
import com.project.website.dto.Entry;
import com.project.website.dto.LeaderboardDto;
import com.project.website.dto.PostDto;
import com.project.website.dto.RecipeResponse;
import com.project.website.service.CompetitionService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/competition")
@AllArgsConstructor
public class CompetitionController {
	private final CompetitionService competitionService;

	@GetMapping("/")
	public ResponseEntity<Page<CompetitionDto>> getAllCompetitions(@RequestParam int page, @RequestParam int elements) {

		return status(HttpStatus.OK).body(competitionService.getAllCompetitions(page, elements));
	}

	@GetMapping("/{competitionId}")
	public ResponseEntity<CompetitionDto> getCompetition(@PathVariable Long competitionId) {

		return status(HttpStatus.OK).body(competitionService.getCompetition(competitionId));
	}

	@PostMapping
	public ResponseEntity<Void> entry(@RequestBody Entry entry) {
		competitionService.entry(entry);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/leaderboard")
	public ResponseEntity<Page<LeaderboardDto>> leaderboard(@RequestParam Long competitionId, @RequestParam int page,
			@RequestParam int elements) {

		return status(HttpStatus.OK).body(competitionService.leaderboard(competitionId, page, elements));
	}

	@GetMapping("/recipeParticipants")
	public ResponseEntity<Page<RecipeResponse>> recipeParticipants(@RequestParam Long competitionId,
			@RequestParam int page, @RequestParam int elements) {
		return status(HttpStatus.OK).body(competitionService.recipeParticipants(competitionId, page, elements));

	}

	@GetMapping("/postParticipants")
	public ResponseEntity<Page<PostDto>> postParticipants(@RequestParam Long competitionId, @RequestParam int page,
			@RequestParam int elements) {
		return status(HttpStatus.OK).body(competitionService.postParticipants(competitionId, page, elements));

	}

	@GetMapping("/active")
	public ResponseEntity<Page<CompetitionDto>> getAllActiveCompetitions(@RequestParam int page,
			@RequestParam int elements) {

		return status(HttpStatus.OK).body(competitionService.getAllActiveCompetitions(page, elements));
	}

}
