package com.project.website.service;

import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.project.website.Model.Question;
import com.project.website.Model.User;
import com.project.website.Repository.QuestionRepository;
import com.project.website.dto.QuestionResponse;
import com.project.website.exceptions.RecipeException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QuestionService {
	private final QuestionRepository questionRepository;
	private final AuthService authService;

	public void save(String text, String subject, MultipartFile[] images) {
		questionRepository.save(map(text, subject, images, authService.getCurrentUser()));
	}

	private Question map(String text, String subject, MultipartFile[] images, User currentUser) {

		return Question.builder().createdDate(Instant.now()).text(text).subject(subject)
				.user(authService.getCurrentUser()).approved(false).images(RecipeService.imagesAsList(images)).build();

	}

	private QuestionResponse mapToDto(Question question) {
		return QuestionResponse.builder().createdDate(question.getCreatedDate()).subject(question.getSubject())
				.text(question.getText()).userName(question.getUser().getUsername())
				.images(RecipeService.listAsImages(question.getImages())).userId(question.getUser().getUserId())
				.duration(RecipeService.getDuration(question.getCreatedDate()))
				.build();

	}

	@Transactional(readOnly = true)
	public Page<QuestionResponse> getAllQuestion(Integer page, int elements) {
		return questionRepository.findByApproved(true, PageRequest.of(page, elements)).map(this::mapToDto);
	}

	@Transactional(readOnly = true)
	public QuestionResponse getQuestion(Long questionId) {
		return mapToDto(
				questionRepository.findById(questionId).orElseThrow(() -> new RecipeException("Question not found")));
	}

}
