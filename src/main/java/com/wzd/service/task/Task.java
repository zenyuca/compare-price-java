package com.wzd.service.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 * 
 * @author WeiZiDong
 *
 */
@Component
public class Task {
	private static final Logger log = LogManager.getLogger(Task.class);
	// @Autowired
	// private FileService fileService;
	
	// 每天定时清理下载文件夹
	public void deleteFile() {
		
	}
}
