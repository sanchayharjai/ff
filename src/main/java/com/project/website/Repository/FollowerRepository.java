package com.project.website.Repository;

import com.project.website.Model.Followers;
import com.project.website.Model.User;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowerRepository extends JpaRepository<Followers, Long> {

	Optional<Followers> findByUser(User currentUser);

	void deleteByUser(User user);

	Optional<Followers> findByUserAndFollowedUserId(User currentUser, long l);

	Page<Followers> findByFollowedUserId(Long userId, Pageable page);

	Page<Followers> findByUser(User user, Pageable page);

}
