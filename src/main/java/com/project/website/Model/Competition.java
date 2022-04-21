package com.project.website.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Competition {

	@Id
	private Long competitionId;

	private String topic;

	@Lob
	private String about;


	private String duration;
	
	@Lob
	private String rules;
	
	private boolean approved;
	
	private String winner;
	
	private Long winnerUserId;
	
	private boolean recipe;
	
	private boolean active;
	
	private String imageS;
	
	
	
}
