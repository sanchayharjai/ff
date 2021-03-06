package com.project.website.Model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Likes {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long likeId;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "postId", referencedColumnName = "postId")
	private Post post;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId", referencedColumnName = "userId")
	private User user;

}
