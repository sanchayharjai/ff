package com.project.website.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.website.Model.Competition;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long> {
	Page<Competition> findAll(Pageable page);

	Page<Competition> findByActive(boolean active, Pageable page);

	Page<Competition> findByApproved(boolean active, Pageable page);
}
