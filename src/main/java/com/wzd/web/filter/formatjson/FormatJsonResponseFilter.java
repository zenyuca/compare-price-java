package com.wzd.web.filter.formatjson;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.wzd.web.dto.response.ResponseCode;
import com.wzd.web.dto.response.RestResponse;

/**
 * rest响应过滤器，用于将返回值转为RestResponse
 * 
 * @author WeiZiDong
 *
 */
@Priority(10)
public class FormatJsonResponseFilter implements ContainerResponseFilter {
//	private static final Logger log = LogManager.getLogger(FormatJsonResponseFilter.class);
	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		try {

			MediaType mediaType = responseContext.getMediaType();
			if (mediaType != null && !MediaType.APPLICATION_JSON_TYPE.equals(mediaType)) {
				return;
			}

			Object responseData = responseContext.getEntity();
//			log.debug("responseData=" + responseData);

			RestResponse jsonResponse;

			if (responseData instanceof RestResponse) {
				jsonResponse = (RestResponse) responseData;
			} else {
				jsonResponse = new RestResponse(ResponseCode.成功);
				jsonResponse.setData(responseData);
			}
//			log.debug("restResponse=" + jsonResponse);

			responseContext.setStatus(ResponseCode.成功.getCode());

			responseContext.setEntity(jsonResponse);

		} catch (Exception e) {
			throw new RuntimeException("转换响应统一格式发生异常", e);
		}

	}

}
