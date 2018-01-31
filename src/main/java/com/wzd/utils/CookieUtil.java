package com.wzd.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 客户端Cookie工具类
 * 
 * @author lujuzhi
 * 
 */
public class CookieUtil {

	/**
	 * 根据cookie名称获取Cookie对象
	 * 
	 * @param name
	 *            cookie名称
	 * @param request
	 * @return
	 */
	public static Cookie getCookie(HttpServletRequest request) {

		Cookie cookies[] = request.getCookies();

		if (null == cookies || cookies.length == 0) {
			return null;
		}

		return cookies[0];
	}

	/**
	 * 获取cookie中的value值
	 * 
	 * @param request
	 * @return
	 */
	public static String getCookieValue(HttpServletRequest request) {

		Cookie cookie = getCookie(request);

		if (null != cookie) {

			return cookie.getValue();

		}

		return null;

	}

	public static Cookie getCookie(HttpServletRequest request, String cookieName) {

		Cookie cookies[] = request.getCookies();

		if (null == cookies) {
			return null;
		}

		for (int i = 0; i < cookies.length; i++) {
			if (cookieName.equals(cookies[i].getName())) {
				return cookies[i];
			}
		}

		return null;

	}

	public static String getCookieValue(HttpServletRequest request, String cookieName) {
		Cookie cookie = getCookie(request, cookieName);
		if (null != cookie) {

			return cookie.getValue();

		}

		return null;
	}

	/**
	 * 删除Cookie对象
	 * 
	 * @param cookie
	 *            Cookie对象
	 * @param request
	 * @param response
	 */
	public static void deleteCookie(Cookie cookie, HttpServletRequest request, HttpServletResponse response) {

		if (null != cookie) {
			cookie.setPath(getPath(request));
			cookie.setValue(null);
			cookie.setMaxAge(0); // 设置生命周期为0，不能为负数
			response.addCookie(cookie);
		}

	}

	/**
	 * 删除所有的Cookie对象
	 */
	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookies[] = request.getCookies();
		if (null != cookies && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				cookie.setPath(getPath(request));
				cookie.setValue(null);
				cookie.setMaxAge(0); // 设置生命周期为0，不能为负数
				response.addCookie(cookie);
			}
		}
	}

	/**
	 * 设置Cookie对象，默认30分钟有效期
	 * 
	 * @param name
	 *            cookie名称
	 * @param value
	 *            cookie值
	 * @param request
	 * @param response
	 */
	public static void setCookie(String name, String value, HttpServletRequest request, HttpServletResponse response) {
		setCookie(name, value, 1800, request, response);
	}

	/**
	 * 设置Cookie对象
	 * 
	 * @param name
	 *            cookie名称
	 * @param value
	 *            cookie值
	 * @param maxAge
	 *            cookie有效期，单位为秒
	 * @param request
	 * @param response
	 */
	public static void setCookie(String name, String value, int maxAge, HttpServletRequest request, HttpServletResponse response) {

		// 创建Cookie
		Cookie cookie = new Cookie(name, value == null ? "" : value);
		cookie.setMaxAge(maxAge);
		cookie.setPath(getPath(request));
		response.addCookie(cookie);

		// System.out.println();
		// System.out.println("创建Cookie [name=" + name + "]");
	}

	/**
	 * 修改Cookie对象的value值
	 * 
	 * @param cookie
	 *            cookie对象
	 * @param value
	 *            cookie值
	 * @param request
	 * @param response
	 */
	public static void setCookie(String value, HttpServletRequest request, HttpServletResponse response) {

		Cookie cookie = getCookie(request);

		if (null != cookie) {
			// 修改Cookie
			cookie.setValue(value);
			cookie.setPath(getPath(request));
			response.addCookie(cookie);
		}

	}

	/**
	 * 获取上下文路径
	 * 
	 * @param request
	 * @return
	 */
	private static String getPath(HttpServletRequest request) {
		String path = request.getContextPath();
		return (path == null || path.length() == 0) ? "/" : path;
	}

}
