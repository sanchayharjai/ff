package com.project.website.Repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.website.Model.Competition;
import com.project.website.Model.Entries;
import com.project.website.Model.Post;
import com.project.website.Model.Recipe;
import com.project.website.Model.User;

@Repository
public interface EntriesRepository extends JpaRepository<Entries, Long> {

	Optional<Entries> findByRecipeAndUser(Recipe recipe, User currentUser);
	
	Optional<Entries> findByPostAndUser(Post post, User currentUser);

	Optional<Entries> findByCompetitionAndUser(Competition competition, User currentUser);

	Page<Entries> findByCompetition(Competition competition,Pageable page);

	void deleteBypost(Post orElseThrow);

}
