package com.wzd.web.dto.response;

import com.alibaba.fastjson.JSON;
import tk.mybatis.mapper.util.StringUtil;

/**
 * rest 响应结果
 * 
 * @author WeiZiDong
 *
 */
public class RestResponse {

	// 状态
	private int code;

	// 返回信息
	private String msg;

	// 响应数据
	private Object data;

	public RestResponse() {
	}

	/**
	 * 构造函数
	 * 
	 * @param responseCode
	 */
	public RestResponse(ResponseCodeType responseCode) {
		this(responseCode, null);
	}

	/**
	 * 构造函数
	 * 
	 * @param responseCode
	 * @param msg
	 */
	public RestResponse(ResponseCodeType responseCode, String msg) {
		this.code = responseCode.getCode();
		this.msg = generateMsg(responseCode, msg);
	}

	/**
	 * 构造函数
	 * 
	 * @param responseCode
	 * @param msg
	 */
	public RestResponse(int responseCode, String msg) {
		this.code = responseCode;
		this.msg = msg;
	}

	/**
	 * 获取数据
	 * 
	 * @return
	 */
	public Object getData() {
		return data;
	}

	/**
	 * 获取状态
	 * 
	 * @return
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * 获取信息
	 * 
	 * @return
	 */
	public String getMsg() {
		return this.msg;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public static String generateMsg(ResponseCodeType responseCode, String msg) {

		return responseCode.getMsg() + (StringUtil.isEmpty(msg) ? "" : "<" + msg + ">");
	}
}
