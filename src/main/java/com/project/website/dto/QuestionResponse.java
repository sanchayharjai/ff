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
public class QuestionResponse {

	private String text;
	private String subject;
	private Instant createdDate;
	private byte[][] images;
	private String userName;
	private byte[] profilePic;
	private String duration;
	private Long userId;

}
