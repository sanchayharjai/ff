package com.project.website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.project.website.Repository.RecipeRepository;
import com.project.website.config.SwaggerConfiguration;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@Import(SwaggerConfiguration.class)
public class WebsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsiteApplication.class, args);
	
	}

} 