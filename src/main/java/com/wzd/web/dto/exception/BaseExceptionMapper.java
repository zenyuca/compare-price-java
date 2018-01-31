package com.wzd.web.dto.exception;

import com.wzd.web.dto.response.ResponseCode;
import com.wzd.web.dto.response.RestResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonProcessingException;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * REST 异常映射
 * 
 * 将异常转换成RestResponse
 * 
 * @author WeiZiDong
 *
 */
public class BaseExceptionMapper implements ExceptionMapper<Exception> {
	private static final Logger log = LogManager.getLogger(BaseExceptionMapper.class);

	@Override
	public Response toResponse(Exception e) {

		RestResponse response = null;

		if (e instanceof WebException) {
			WebException baseException = ((WebException) e);
			response = new RestResponse(baseException.getResponseCode(), baseException.getMsg());
		} else if (e instanceof NotFoundException) {
			response = new RestResponse(ResponseCode.找不到路径);
		} else if (e instanceof NotAllowedException) {
			response = new RestResponse(ResponseCode.禁止访问);
		} else if (e instanceof JsonProcessingException) {
			response = new RestResponse(ResponseCode.错误JSON);
		} else if (e instanceof NotSupportedException) {
			response = new RestResponse(ResponseCode.不支持的媒体类型);
		} else {
			response = new RestResponse(ResponseCode.服务器异常);
		}
		log.error(response.getMsg(), e);

		return Response.status(response.getCode()).type(MediaType.APPLICATION_JSON).entity(response).build();
	}

}
