package com.project.website.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowerDto {
	private Long userId;
	private byte[] profilePic;
	private String username;
	private boolean isFollowed;
	private boolean currUser;
	private String profileI;
}
