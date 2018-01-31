package com.wzd.service.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class TimerTask implements ApplicationListener<ContextRefreshedEvent> {
	private static final Logger log = LogManager.getLogger(TimerTask.class);
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
	}

	

}
