package com.project.website.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.project.website.dto.FavouriteDto;
import com.project.website.exceptions.RecipeException;
import com.project.website.Model.Favourite;
import com.project.website.Model.Recipe;
import com.project.website.Model.User;
import com.project.website.Repository.FavouriteRepository;
import com.project.website.Repository.RecipeRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FavouriteService {
	private final RecipeRepository recipeRepository;
	private final AuthService authService;
	private final FavouriteRepository favouriteRepository;

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void addToFavourite(FavouriteDto favouriteDto) {

		Recipe recipe = recipeRepository.findByrecipeIdAndApproved(favouriteDto.getRecipeId(), true).orElseThrow(() -> new RecipeException(""));
		Optional<Favourite> favourite = favouriteRepository.findByRecipeAndUser(recipe, authService.getCurrentUser());
		if (favourite.isPresent()) {
			favouriteRepository.deleteById(favourite.get().getFavouriteId());
		} else {
			favouriteRepository.save(mapToFavourite(recipe, authService.getCurrentUser()));
		}
	}

	private Favourite mapToFavourite(Recipe recipe, User user) {
		return Favourite.builder().recipe(recipe).user(user).build();
	}

}
