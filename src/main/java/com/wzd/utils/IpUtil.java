package com.wzd.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

/**
 * IP操作工具类
 * 
 * @author WeiZiDong
 * 
 */
public class IpUtil {

	/**
	 * 获取客户端IP地址，支持代理服务器
	 * 
	 * @param request
	 * @return
	 */
	public static final String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		String localIP = "127.0.0.1";
		if ((ip == null) || (ip.length() == 0) || (ip.equalsIgnoreCase(localIP)) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if ((ip == null) || (ip.length() == 0) || (ip.equalsIgnoreCase(localIP)) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if ((ip == null) || (ip.length() == 0) || (ip.equalsIgnoreCase(localIP)) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 获取服务器主机hostname
	 * 
	 * @param request
	 * @return
	 */
	public static final String getServerHostname(HttpServletRequest request) {
		StringBuffer url = request.getRequestURL();
		String contextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
		return contextUrl;
	}

	/**
	 * 获取服务器主机hostname（url后不带“/”）
	 * 
	 * @param request
	 * @return
	 */
	public static final String getServerHostnameRoot(HttpServletRequest request) {
		StringBuffer url = request.getRequestURL();
		String path = request.getServletPath() + request.getPathInfo();
		String contextUrl = url.delete(url.length() - path.length(), url.length()).toString();
		return contextUrl;
	}

	// 获得本机ip
	public static String getClient_Ip() {
		String ip = "";
		try {
			InetAddress address = InetAddress.getLocalHost();
			ip = address.getHostAddress();
		} catch (UnknownHostException e) {
			System.out.println("服务器未连网");
		}
		return ip;
	}
}
