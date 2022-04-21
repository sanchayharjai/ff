package com.project.website.controller;

import static org.springframework.http.ResponseEntity.status;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.project.website.service.QuestionService;
import com.project.website.dto.QuestionResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/question")
@AllArgsConstructor
public class QuestionController {
	private final QuestionService questionService;

	@PostMapping
	public ResponseEntity<Void> createQuestion(@RequestParam String text, @RequestParam String subject,
			@RequestParam MultipartFile[] images) {
		questionService.save(text, subject, images);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/")
	public ResponseEntity<Page<QuestionResponse>> getAllQuestion(@RequestParam Integer page,
			@RequestParam int elements) {
		return status(HttpStatus.OK).body(questionService.getAllQuestion(page, elements));
	}

	@GetMapping("/{questionId}")
	public ResponseEntity<QuestionResponse> getQuestion(@PathVariable Long questionId) {
		return status(HttpStatus.OK).body(questionService.getQuestion(questionId));

	}
}
