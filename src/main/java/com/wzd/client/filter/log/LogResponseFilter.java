package com.wzd.client.filter.log;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogResponseFilter implements ClientResponseFilter {
	private static final Logger log = LogManager.getLogger(LogResponseFilter.class);

	@Override
	public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {

		long startTs = (long) requestContext.getProperty(LogRequestFilter.START_TS_PROPERTY);
		long endTs = System.currentTimeMillis();
		// String type = responseContext.getHeaderString("content-type");
		log.info("响应请求：" + requestContext.getUri());
		// if ("application/json".equals(type)) {
		// log.info("响应结果：" +
		// JSON.toJSONString(responseContext.getEntityStream()));
		// }
		log.info("耗时(ms):" + (endTs - startTs));
	}

}
