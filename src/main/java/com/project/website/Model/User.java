package com.project.website.Model;

import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

	@Id
	@GeneratedValue(generator = MyGenerator.generateId)
	@GenericGenerator(name = MyGenerator.generateId, strategy = "com.project.website.Model.MyGenerator")
	private long userId;

	@NotBlank(message = "Username is required")
	private String username;

	private String name;

	@NotBlank(message = "Password is required")
	private String password;

	@Email
	@NotEmpty(message = "Email is required")
	private String email;

	private Instant created;

	private boolean enabled;

	@Lob
	private String profileImageL;

	@Lob
	private String profileImageS;

	private Long followers;

	@Lob
	private String about;

	private float balance;

	private Integer wBal;
	
	@Lob
	private String details;
	
	private Long tViews;
	
	private Float tEarnings;
	
	private Boolean rewards;

}
