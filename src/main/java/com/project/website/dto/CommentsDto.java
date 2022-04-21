package com.project.website.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentsDto {

	private Long id;

	private Long recipeId;

	private String text;

	private String username;

	private Long userId;

	private String duration;

	private Long postId;

	private boolean byCurrUser;
	
	private String profileI;

}
