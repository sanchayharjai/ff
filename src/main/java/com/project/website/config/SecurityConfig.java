package com.project.website.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.project.website.security.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;

@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Override
	public void configure(HttpSecurity httpsecurity) throws Exception {
		httpsecurity.cors().and().csrf().disable().authorizeRequests().antMatchers("/api/auth/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/recipe/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/recipe/").permitAll()
				.antMatchers(HttpMethod.GET, "/api/recipe/by-user/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/recipe/by-category/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/recipe/search/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/recipe/top").permitAll()
				.antMatchers(HttpMethod.GET, "/api/recipe/similar").permitAll()
				.antMatchers(HttpMethod.GET, "/api/answer/by-question/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/comment/by-recipe/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/comment/by-user/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/comment/by-post/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/question/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/question/").permitAll()
				.antMatchers(HttpMethod.GET, "/api/competition/").permitAll()
				.antMatchers(HttpMethod.GET, "/api/competition/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/post/").permitAll()
				.antMatchers(HttpMethod.GET, "/api/post/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/user/profile").permitAll()
				.antMatchers(HttpMethod.POST, "/api/user/forgotpassword").permitAll()
				.antMatchers(HttpMethod.POST, "/api/recipe/test").permitAll()
				.antMatchers(HttpMethod.POST, "/api/recipe/video").permitAll()
				.antMatchers(HttpMethod.GET, "/api/user/followedby").permitAll()
				.antMatchers(HttpMethod.GET, "/api/user/passwordVerification/**").permitAll()
				.antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/security",
						"/swagger-ui.html", "/webjars/**")
				.permitAll().anyRequest().authenticated();

		httpsecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

	}

	private final UserDetailsService userDetailsService;

	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
