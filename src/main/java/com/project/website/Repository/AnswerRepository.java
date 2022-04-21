package com.project.website.Repository;

import com.project.website.Model.Answer;
import com.project.website.Model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long>{

	Page<Answer> findByQuestion(Question question,Pageable page);

	

}
