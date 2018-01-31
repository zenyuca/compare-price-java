package com.wzd.web.dto.exception;

import com.wzd.web.dto.response.ResponseCodeType;
import com.wzd.web.dto.response.RestResponse;

@SuppressWarnings("serial")
public class WebException extends RuntimeException {

	private int responseCode;

	private String msg;

	/**
	 * 构造函数
	 * 
	 * @param responseCode
	 * @param msg
	 */
	public WebException(ResponseCodeType responseCode) {
		this(responseCode, null, null);
	}
	
	/**
	 * 构造函数
	 * 
	 * @param responseCode
	 * @param msg
	 */
	public WebException(ResponseCodeType responseCode, String msg) {
		this(responseCode, msg, null);
	}

	/**
	 * 构造函数
	 * 
	 * @param responseCode
	 * @param msg
	 * @param cause
	 */
	public WebException(ResponseCodeType responseCode, String msg, Throwable cause) {
		super(RestResponse.generateMsg(responseCode, msg), cause);

		this.responseCode = responseCode.getCode();
		this.msg = RestResponse.generateMsg(responseCode, msg);
	}

	/**
	 * 构造函数
	 * 
	 * @param responseCode
	 * @param msg
	 * @param cause
	 */
	public WebException(int responseCode, String msg, Throwable cause) {
		super(msg, cause);

		this.responseCode = responseCode;
		this.msg = msg;
	}

	/**
	 * 构造函数
	 * 
	 * @param responseCode
	 * @param msg
	 */
	public WebException(int responseCode, String msg) {
		this(responseCode, msg, null);

		this.responseCode = responseCode;
		this.msg = msg;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getMsg() {
		return this.msg;
	}

}
