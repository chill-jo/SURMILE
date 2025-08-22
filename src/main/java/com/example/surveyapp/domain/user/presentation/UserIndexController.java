package com.example.surveyapp.domain.user.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserIndexController {

	@GetMapping("/")
	public String index() {
		log.info("index.html");
		return "forward:/index.html";
	}
}
