package com.project.website.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerDto {
	private String answer;
	private Long questionId;
	private String userName;
	private Long userId;
	private String duration;
	private byte[] profilePic;
	private String profilePicType;

}
