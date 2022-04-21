package com.project.website.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.website.Model.Question;
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

	Page<Question> findByApproved(boolean approved, Pageable page);

}
