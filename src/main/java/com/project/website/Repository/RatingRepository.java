package com.project.website.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.website.Model.Rating;
import com.project.website.Model.Recipe;
import com.project.website.Model.User;
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

	Optional<Rating> findByRecipeAndUser(Recipe recipe, User currentUser);

}
