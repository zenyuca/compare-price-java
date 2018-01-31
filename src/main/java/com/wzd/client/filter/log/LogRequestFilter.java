package com.wzd.client.filter.log;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;

public class LogRequestFilter implements ClientRequestFilter {
	private static final Logger log = LogManager.getLogger(LogRequestFilter.class);

	public static final String START_TS_PROPERTY = "_start_ts";

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {

		log.info("发送请求到：" + requestContext.getUri());
		log.info("参数：" + JSON.toJSONString(requestContext.getEntity()));
		long startTs = System.currentTimeMillis();

		requestContext.setProperty(START_TS_PROPERTY, startTs);
	}

}
