package com.project.website.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.project.website.Model.Post;
import com.project.website.Model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	Page<Post> findByApproved(Pageable page, boolean approved);

	@Query
	Page<Post> findByUserAndApproved(User user, boolean approved, Pageable page);

	void deleteByPostIdAndUser(Long postId, User currentUser);

	Optional<Post> findBypostIdAndUser(Long postId,User user);

}
