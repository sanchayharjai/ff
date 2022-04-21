package com.project.website.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponse {
	private Long id;
	private String recipeName;
	private String url;
	private String steps;
	private String userName;
	private long voteCount;
	private String commentCount;
	private String duration;
	private String nutritionData;
	private Float rating;
	private String timeTaken;
	private String ingredients;
	private String description;
	private String category;
	private Instant createdDate;
	private Integer servings;
	private Long userId;
	private boolean upVote;
	private boolean downVote;
	private boolean favourite;
	private boolean rated;
	private Integer ratingByUser;
	private Long rateCountn;
	private Long rateTotal;
	private String rateCount;
	private boolean isFollowed;
	private boolean currUser;
	private String thumbnailS;
	private String thumbnailL;
	private String[] image;
	private String profileImage;
	private Long views;
	private String videoId;
}
