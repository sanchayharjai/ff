package com.project.website.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.website.Model.Recipe;
import com.project.website.Model.User;
import com.project.website.Model.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

	Optional<Vote> findTopByRecipeAndUserOrderByVoteIdDesc(Recipe recipe, User currentUser);
	

}
