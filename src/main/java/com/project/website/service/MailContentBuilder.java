package com.project.website.service;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailContentBuilder {
	private final TemplateEngine templateEngine;

	String build(String message,String topic, String token) {
		Context context = new Context();
		context.setVariable("message", message);
		context.setVariable("topic", topic);
		context.setVariable("token", token);

		return templateEngine.process("mailTemplate", context);

	}
}
