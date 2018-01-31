package com.wzd.web.filter.log;

/**
 * 日志事务类型
 * 
 * @author WeiZiDong
 *
 */
public enum RequestLogType {

	/**
	 * 需要事务
	 */
	REQUIRED,
	/**
	 * 不需要事务
	 */
	NOTSUPPORTED
}
