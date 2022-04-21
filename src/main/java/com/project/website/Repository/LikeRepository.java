package com.project.website.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.website.Model.Likes;
import com.project.website.Model.Post;
import com.project.website.Model.User;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {

	Optional<Likes> findByPostAndUser(Post post, User currentUser);

	void deleteByPost(Post post);

	Optional<Likes> findTopByPostAndUserOrderByLikeIdDesc(Post post, User currentUser);

}
