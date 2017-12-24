package com.cn.demo;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class LogDemo {

	private Logger logger = LoggerFactory.getLogger(LogDemo.class);
	
	@PostConstruct
	public void logInfo(){
		logger.info("=========== demo log info ===========");
	}
}
