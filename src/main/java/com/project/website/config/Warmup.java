package com.project.website.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.website.dto.tokenDto;
import com.project.website.service.AuthService;
import com.project.website.service.RefreshTokenService;

import lombok.AllArgsConstructor;
@RestController

@AllArgsConstructor
public class Warmup {
	@GetMapping("/_ah/warmup")
	public ResponseEntity<Void> warmupreq() {
	
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
