package com.herasimau.bot;

import com.herasimau.bot.service.VkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ChatBotApplication {

	@Autowired
	VkService vkService;

	public static void main(String[] args) {
		SpringApplication.run(ChatBotApplication.class, args);
	}
}
