package com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;


public class MessageController {
	
private static final Logger log = LoggerFactory.getLogger(MessageController.class);
	
	@RequestMapping("/test")
	public String testMessage() {
		log.info("hello, its an info");
		log.debug("hello, its an debug");
		return "test";
	}
	@RequestMapping("/hello")
	public String sayHello() {
		//System.out.println("Hello from test app");
		log.info("hello from test app1");
		return "hello from bootcamp";
	}

}
