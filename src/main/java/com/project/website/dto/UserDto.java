package com.project.website.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
	private Long userId;
	private String userName;
	private String name;
	private Long followers;
	private Long following;
	private String about;
	private boolean isFollowed;
	private boolean currUser;
	private String profileImageL;
	private Float balance;
}
