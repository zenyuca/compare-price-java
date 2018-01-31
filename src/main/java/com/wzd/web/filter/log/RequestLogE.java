package com.wzd.web.filter.log;

import org.dom4j.tree.AbstractEntity;

/**
 * 请求日志 实体类
 * 
 * @author WeiZiDong
 *
 */
@SuppressWarnings("serial")
public class RequestLogE extends AbstractEntity {

	// 请求地址
	private String uri;

	// 请求内容
	private String content;

	// 请求时间
	private Long requestTs;

	// 响应时间
	private Long responseTs;

	// 响应代码
	private Integer responseCode;

	// 响应内容
	private String response;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Long getRequestTs() {
		return requestTs;
	}

	public void setRequestTs(Long requestTs) {
		this.requestTs = requestTs;
	}

	public Long getResponseTs() {
		return responseTs;
	}

	public void setResponseTs(Long responseTs) {
		this.responseTs = responseTs;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

}
