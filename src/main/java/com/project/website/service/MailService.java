package com.project.website.service;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.project.website.exceptions.RecipeException;
import com.project.website.Model.NotificationEmail;;

@Service
@AllArgsConstructor
@Slf4j
public class MailService {
	private final JavaMailSender mailSender;
	private final MailContentBuilder mailContentBuilder;

	@Async
	public void sendMail(String email,String message,String topic,String token) throws RecipeException {
		MimeMessagePreparator messagePreparator = new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true,"UTF-8"){

				};
				
			
				messageHelper.setFrom("support@foodyfolks.com");
				messageHelper.setTo(email);
				messageHelper.setSubject(topic);
				messageHelper.setText(mailContentBuilder.build(message,topic,token),true);
			    
			
			}
		};
		try {
			mailSender.send(messagePreparator);
			log.info("Activation email sent!!");
		} catch (MailException e) {
			throw e;
		}
	}
}
