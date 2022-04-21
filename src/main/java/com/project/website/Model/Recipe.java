package com.project.website.Model;

import java.time.Instant;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

	@Id
	@GeneratedValue(generator = MyGenerator.generateId)
	@GenericGenerator(name = MyGenerator.generateId, strategy = "com.project.website.Model.MyGenerator")
	private Long recipeId;

	@NotBlank(message = "Recipe name cannot be empty")
	private String recipeName;

	@Nullable
	private String url;

	@Nullable
	@Lob
	private String steps;
	@Lob
	private String description;
	@Lob
	private String ingredients;

	private Integer voteCount;

	@Nullable
	@Lob
	private String nutritionData;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId", referencedColumnName = "userId")
	private User user;

	private Instant createdDate;

	private boolean approved;

	private Long ratingCount;

	private Long ratingTotal;
	@Nullable
	private String timeTaken;
	@Nullable
	private String category;

	@Nullable
	private Integer servings;

	@Lob
	private String image;

	@Nullable

	@Lob
	private String thumbnailS;
	@Lob
	private String thumbnailL;
	@Lob
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "recipeId", referencedColumnName = "recipeId")
	private List<Ip> ip;
	
	private Long video;
	
	private Long views;

}
