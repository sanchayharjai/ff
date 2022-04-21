package com.project.website.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashDto {
	
	private String username;
	
	private String pImage;
	
	private Float tEarnings;
	
	private Float balance;
	
	private Long tViews;
	
	private Long followers;
	
	private Long recipes;
	
	private Long comments;
	
	private String about;
}
