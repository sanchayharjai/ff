package com.project.website.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeDashDto {
	
	private String createdDate;
	private String recipeName;
	private String thumbnail;
	private Long views;
	private Long commentCount;
	private Integer voteCount;

}
