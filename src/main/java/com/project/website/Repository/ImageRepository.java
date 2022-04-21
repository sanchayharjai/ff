package com.project.website.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.website.Model.Images;

@Repository
public interface ImageRepository extends JpaRepository<Images, Long> {

	void deleteByPostId(Long postId);
}
