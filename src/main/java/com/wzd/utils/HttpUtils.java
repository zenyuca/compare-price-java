package com.wzd.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.wzd.web.dto.exception.WebException;
import com.wzd.web.dto.response.ResponseCode;

/**
 * http工具类
 * 
 * @author WeiZiDong
 *
 */
public class HttpUtils {

	/**
	 * url解码
	 * 
	 * @param str
	 * @return
	 */
	public static String ParseUrl(String str) {
		try {
			return URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new WebException(ResponseCode.不允许此方法, "url解码失败");
		}
	}
}
