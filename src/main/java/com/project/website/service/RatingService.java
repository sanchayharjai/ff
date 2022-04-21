package com.project.website.service;

import com.project.website.Model.Rating;
import com.project.website.Model.Recipe;
import com.project.website.Repository.RatingRepository;
import com.project.website.Repository.RecipeRepository;
import com.project.website.dto.RateDto;
import com.project.website.exceptions.RecipeException;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RatingService {
	private final RecipeRepository recipeRepository;
	private final AuthService authService;
	private final RatingRepository ratingRepository;

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void rate(RateDto rateDto) {
		Recipe recipe = recipeRepository.findById(rateDto.getRecipeId()).orElseThrow(() -> new RecipeException(""));
		Optional<Rating> ratingByRecipeAndUser = ratingRepository.findByRecipeAndUser(recipe,
				authService.getCurrentUser());
		if (ratingByRecipeAndUser.isPresent()) {
			recipe.setRatingTotal(
					recipe.getRatingTotal() - ratingByRecipeAndUser.get().getRateCount() + rateDto.getRating());
			ratingRepository.deleteById(ratingByRecipeAndUser.get().getRateId());
		} else {
			recipe.setRatingCount(recipe.getRatingCount() + 1);
			recipe.setRatingTotal(recipe.getRatingTotal() + rateDto.getRating());
		}
		recipeRepository.save(recipe);
		ratingRepository.save(mapToRating(rateDto, recipe));

	}

	private Rating mapToRating(RateDto rateDto, Recipe recipe) {
		return Rating.builder().rateCount(rateDto.getRating()).recipe(recipe).user(authService.getCurrentUser())
				.build();
	}
}
