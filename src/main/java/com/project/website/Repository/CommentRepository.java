package com.project.website.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.website.Model.Comment;
import com.project.website.Model.Post;
import com.project.website.Model.Recipe;
import com.project.website.Model.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	Page<Comment> findByRecipe(Recipe recipe, Pageable page);

	Page<Comment> findAllByUser(User user, Pageable page);

	Page<Comment> findByPost(Post post, Pageable page);

	void deleteByPostAndUser(Post post, User currentUser);

}
