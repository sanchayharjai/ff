package com.project.website.controller;

import com.project.website.dto.AnswerDto;
import com.project.website.service.AnswerService;
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
import lombok.AllArgsConstructor;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/answer")
@AllArgsConstructor
public class AnswerController {
	private final AnswerService answerService;

	@PostMapping
	public ResponseEntity<Void> createAnswer(@RequestBody AnswerDto answerDto) {
		answerService.save(answerDto);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/by-question/{questionId}")
	public ResponseEntity<Page<AnswerDto>> getAllAnswers(@PathVariable Long questionId, @RequestParam Integer page) {

		return status(HttpStatus.OK).body(answerService.getAllAnswers(questionId, page));
	}
}
