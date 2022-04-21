package com.project.website.service;

import com.project.website.Model.Answer;
import com.project.website.Model.Question;
import com.project.website.Repository.AnswerRepository;
import com.project.website.Repository.QuestionRepository;
import com.project.website.dto.AnswerDto;
import com.project.website.exceptions.RecipeException;
import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AnswerService {

	private final AuthService authService;
	private final AnswerRepository answerRepository;
	private final QuestionRepository questionRepository;

	public void save(AnswerDto answerDto) {
		Question question = questionRepository.findById(answerDto.getQuestionId()).orElseThrow(() -> new RecipeException(""));
		answerRepository.save(map(answerDto, question));

	}

	public Answer map(AnswerDto answerDto, Question question) {
		return Answer.builder().question(question).text(answerDto.getAnswer()).createdDate(Instant.now())
				.user(authService.getCurrentUser()).build();
	}

	public AnswerDto mapEntityDto(Answer answer) {
		return AnswerDto.builder().answer(answer.getText()).userId(answer.getUser().getUserId())
				.userName(answer.getUser().getUsername())
			.build();
	}

	@Transactional(readOnly = true)
	public Page<AnswerDto> getAllAnswers(Long questionId, int page) {

		return answerRepository
				.findByQuestion(questionRepository.findById(questionId)
						.orElseThrow(() -> new RecipeException("Question not found")), PageRequest.of(page, 20))
				.map(this::mapEntityDto);

	}

}
