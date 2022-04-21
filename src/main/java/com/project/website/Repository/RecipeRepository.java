package com.project.website.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.project.website.Model.Recipe;
import com.project.website.Model.User;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
	@Query
	Page<Recipe> findByApproved(boolean approved, Pageable page);

	@Query
	Optional<Recipe> findByrecipeIdAndApproved(Long id, boolean b);

	@Query
	Page<Recipe> findByUserAndApproved(User user, boolean approved, Pageable page);

	@Query("select r from Recipe r where r.category like %?1% and r.approved = ?2")
	Page<Recipe> findByCategoryAndApproved(String category, boolean approved, Pageable page);

	@Query("select r from Recipe r where r.recipeName like %?1% and r.approved= ?2")
	Page<Recipe> findByRecipeNameAndApproved(String search, boolean approved, Pageable page);

	@Query("select r from Recipe r where r.approved=?1 order by r.voteCount desc")
	Page<Recipe> findByVoteCountAndApproved(boolean approved, Pageable page);

	@Query(value = "SELECT * FROM recipe AS t1 JOIN (SELECT recipe_id FROM recipe where category like %?1% and approved=?2 ORDER BY RAND() LIMIT ?3) as t2 ON t1.recipe_id=t2.recipe_id  ", nativeQuery = true)
	List<Recipe> findRandom(String category, boolean approved, int limit);

	@Query(value = "SELECT * FROM recipe AS t1 JOIN (SELECT recipe_id FROM recipe where approved=?1 ORDER BY RAND() LIMIT ?2) as t2 ON t1.recipe_id=t2.recipe_id  ", nativeQuery = true)
	List<Recipe> findRandomWithoutCat(boolean approved, int limit);

	@Query(value = "SELECT * FROM recipe where approved =?1 ORDER BY RAND()", nativeQuery = true)
	Page<Recipe> findRandomWithoutCatAndLimit(boolean approved, Pageable page);

	@Query(value = "SELECT * FROM recipe as r join (select followed_user_id from followers where user_id=?2) as f on r.user_id=f.followed_user_id where approved=?1 order by r.recipe_id desc", nativeQuery = true)
	Page<Recipe> findByFollowed(boolean approved, Long currUserId, Pageable page);

	List<Recipe> findByUser(User currentUser);
}
