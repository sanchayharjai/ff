package com.project.website.service;

import com.project.website.Model.Recipe;
import com.project.website.Model.Vote;
import com.project.website.Repository.RecipeRepository;
import com.project.website.Repository.VoteRepository;
import com.project.website.dto.VoteDto;
import com.project.website.exceptions.RecipeException;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import static com.project.website.Model.VoteType.UPVOTE;

@Service
@AllArgsConstructor
public class VoteService {
	private final RecipeRepository recipeRepository;
	private final AuthService authService;
	private final VoteRepository voteRepository;

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void vote(VoteDto voteDto) {
		Recipe recipe = recipeRepository.findById(voteDto.getRecipeId()).orElseThrow(() -> new RecipeException(""));
		Optional<Vote> voteByRecipeAndUser = voteRepository.findTopByRecipeAndUserOrderByVoteIdDesc(recipe,
				authService.getCurrentUser());
		if (voteByRecipeAndUser.isPresent() && voteByRecipeAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
			if (UPVOTE.equals(voteByRecipeAndUser.get().getVoteType())) {
				recipe.setVoteCount(recipe.getVoteCount() - 1);
			} else {
				recipe.setVoteCount(recipe.getVoteCount() + 1);
			}
			voteRepository.deleteById(voteByRecipeAndUser.get().getVoteId());
		} else if (voteByRecipeAndUser.isPresent()
				&& !voteByRecipeAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
			voteRepository.deleteById(voteByRecipeAndUser.get().getVoteId());
			if (UPVOTE.equals(voteDto.getVoteType())) {
				recipe.setVoteCount(recipe.getVoteCount() + 2);
			} else {
				recipe.setVoteCount(recipe.getVoteCount() - 2);
			}
			voteRepository.save(mapToVote(voteDto, recipe));
			recipeRepository.save(recipe);
		}

		else {
			if (UPVOTE.equals(voteDto.getVoteType())) {
				recipe.setVoteCount(recipe.getVoteCount() + 1);
			} else {
				recipe.setVoteCount(recipe.getVoteCount() - 1);
			}
			voteRepository.save(mapToVote(voteDto, recipe));
			recipeRepository.save(recipe);
		}

	}

	private Vote mapToVote(VoteDto voteDto, Recipe recipe) {
		return Vote.builder().voteType(voteDto.getVoteType()).recipe(recipe).user(authService.getCurrentUser()).build();
	}
}
