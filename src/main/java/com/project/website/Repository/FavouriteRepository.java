package com.project.website.Repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.website.Model.Favourite;
import com.project.website.Model.Recipe;
import com.project.website.Model.User;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

	Optional<Favourite> findByRecipeAndUser(Recipe recipe, User user);

	Page<Favourite> findByUser(User user, Pageable page);

}
