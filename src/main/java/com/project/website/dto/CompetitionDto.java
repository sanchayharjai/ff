package com.project.website.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompetitionDto {

	Long competitionId;

	private String topic;

	private String about;

	private String duration;

	private String rules;

	private String winner;

	private Long winnerUserId;

	private boolean recipe;

	boolean enrolled;
	
	private String imageS;

}
