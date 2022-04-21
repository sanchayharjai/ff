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
public class PostDto {
	private Long postId;
	private String text;
	private String url;
	private Instant createdDate;
	private String userName;
	private String duration;
	private Long userId;
	private String commentCount;
	private boolean readonly;
	private String likeCount;
	private boolean isLiked;
	private boolean currUser;
	private boolean media;
	private String[] image;
	private String profileImage;
}
