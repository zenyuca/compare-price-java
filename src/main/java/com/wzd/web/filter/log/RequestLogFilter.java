package com.wzd.web.filter.log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 日志输出
 * 
 * @author WeiZiDong
 *
 */
@Priority(5)
public class RequestLogFilter implements ContainerRequestFilter, ContainerResponseFilter {
	private static final Logger log = LogManager.getLogger(RequestLogFilter.class);

	public static final String REQUEST_LOG_PROPERTY = "REQUEST_LOG_PROPERTY";
	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		RequestLogE requestLog = new RequestLogE();

		UriInfo uriInfo = requestContext.getUriInfo();

		InputStream inputStream = requestContext.getEntityStream();

		String content = convertStreamToString(inputStream);

		ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes());

		requestContext.setEntityStream(stream);

		requestLog.setContent(content);

		requestLog.setUri(uriInfo.getPath());

		requestLog.setRequestTs(System.currentTimeMillis());

		requestContext.setProperty(REQUEST_LOG_PROPERTY, requestLog);

		// log.debug("请求时间：" + requestLog.getRequestTs());
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

		Object requestLogProperty = requestContext.getProperty(RequestLogFilter.REQUEST_LOG_PROPERTY);

		if (requestLogProperty == null) {
			return;
		}

		RequestLogE requestLog = (RequestLogE) requestLogProperty;

		requestLog.setResponseCode(responseContext.getStatus());

		requestLog.setResponse(getResponseParams(responseContext));

		requestLog.setResponseTs(System.currentTimeMillis());
		log.info("调用接口：" + requestLog.getUri() + "\t耗时(ms)：" + (requestLog.getResponseTs() - requestLog.getRequestTs()));
		log.info("参数：" + requestLog.getContent());
//		log.debug("结果：" + requestLog.getResponse());
	}

	/**
	 * 获得响应信息
	 * 
	 * @param responseContext
	 * @return
	 */
	private String getResponseParams(ContainerResponseContext responseContext) {
		Object obj = responseContext.getEntity();
		String responseJson = "";
		try {
			if (obj != null) {
				if (obj instanceof Integer || obj instanceof Double || obj instanceof Float || obj instanceof Long || obj instanceof Boolean || obj instanceof String) {
					responseJson = String.valueOf(obj);
				} else {
					responseJson = obj.toString();
				}
			}
		} catch (Exception e) {
			return responseContext.getEntityStream().toString();
		}

		return responseJson;
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString().trim();
	}

}
