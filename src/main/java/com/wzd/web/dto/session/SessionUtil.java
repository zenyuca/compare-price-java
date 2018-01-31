package com.wzd.web.dto.session;

import com.wzd.model.dao.AdminDao;
import com.wzd.utils.CookieUtil;
import com.wzd.utils.EhcacheUtil;
import com.wzd.utils.MD5Utils;
import com.wzd.utils.SignatureUtil;
import com.wzd.web.dto.exception.WebException;
import com.wzd.web.dto.response.ResponseCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * session工具类
 * 
 * @author WeiZiDong
 *
 */
@Component()
public class SessionUtil {
	private static final Logger log = LogManager.getLogger(SessionUtil.class);
	private static final String SESSION_ID = "session_";
	private static final String ACCESS_TOKEN = "token_";
	private static final Integer COOKIE_MAX_AGE = 3600 * 24 * 7;
	private static final EhcacheUtil ehcache = EhcacheUtil.getInstance();
	@Autowired
	private AdminDao adminDao;

	/**
	 * 从cookie中获取SessionId
	 */
	public static String getSessionIdByCookie(HttpServletRequest request) {
		String appType = request.getParameter("appType");
		return CookieUtil.getCookieValue(request, SESSION_ID + appType);
	}

	/**
	 * 验证debug模式
	 */
	public static Boolean isDebug(HttpServletRequest request) {
		String debug = request.getParameter("debug");
		return debug != null && debug.equals("weizidong");
	}

	/**
	 * 开启debug模式
	 */
	public Session openDebug(HttpServletRequest httpRequest) {
		String appType = httpRequest.getParameter("appType");
		String sessionId = getSessionIdByCookie(httpRequest);
		String accessToken = null;
		Object user = null;
		return generateSession(appType, sessionId, accessToken, user);
	}

	/**
	 * 获取Session
	 */
	public static Session getSession(HttpServletRequest request) {
		Session session = null;
		// 先判断request中否存在，存在既返回
		String appType = request.getParameter("appType");
		log.debug("appType = " + appType);
		Object sessionRequestAttr = request.getAttribute(SESSION_ID + appType);
		if (sessionRequestAttr != null) {
			session = (Session) sessionRequestAttr;
			log.debug("从request中获取到的session = " + session);
			return session;
		}
		String sessionId = getSessionIdByCookie(request);
		log.debug("从cookies中获取到的sessionId = " + sessionId);
		if (StringUtils.isBlank(sessionId)) {
			return null;
		}
		session = ehcache.getSession(sessionId);
		log.debug("从ehcache中获取到的session = " + session);
		return session;
	}

	/**
	 * 获取Session
	 */
	public static Session getSessionById(String sessionId) {
		Session session = null;
		if (StringUtils.isBlank(sessionId)) {
			return null;
		}
		log.debug("sessionId = " + sessionId);
		session = ehcache.getSession(sessionId);
		log.debug("从ehcache中获取到的session = " + session);
		return session;
	}

	/**
	 * 验证是否超时
	 */
	public static void checkTs(Session session, HttpServletRequest request, HttpServletResponse response) {
		if (System.currentTimeMillis() - session.getTs() > COOKIE_MAX_AGE * 1000) {
			removeSession(session.getSessionId(), request, response);
			throw new WebException(ResponseCode.登录超时);
		}
	}

	/**
	 * 验证数字签名
	 */
	public static void checkSignature(Session session, HttpServletRequest request, HttpServletResponse response) {
		String nonce = request.getParameter("nonce");
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		if (StringUtils.isBlank(nonce) || StringUtils.isBlank(signature) || StringUtils.isBlank(timestamp)) {
			removeSession(session.getSessionId(), request, response);
			throw new WebException(ResponseCode.数据签名错误);
		}
		String url = request.getRequestURI();
		String token = MD5Utils.getMD5ofStr(session.getAccessToken() + url);
		if (!SignatureUtil.checkSignature(token, signature, timestamp, nonce)) {
			removeSession(session.getSessionId(), request, response);
			throw new WebException(ResponseCode.数据签名错误);
		}
	}

	/**
	 * 获取当前登陆的账号
	 */
	public static Object getUser(HttpServletRequest request) {
		Session session = getSession(request);
		if (session == null || session.getUser() == null) {
			throw new WebException(ResponseCode.未登录);
		}
		return session.getUser();
	}

	/**
	 * 更新Session
	 */
	public static void updateSession(Session session, HttpServletRequest request) {
		session.setTs(System.currentTimeMillis());
		// 添加到request
		String appType = request.getParameter("appType");
		request.setAttribute(SESSION_ID + appType, session);
		// 写入缓存
		ehcache.putSession(session.getSessionId(), session);
	}

	/**
	 * 保存Session
	 */
	public static void saveSession(Session session, HttpServletRequest request, HttpServletResponse response) {
		session.setTs(System.currentTimeMillis());
		// 写入cookie
		String appType = request.getParameter("appType");
		log.debug("cookie:"+SESSION_ID + appType);
		CookieUtil.setCookie(SESSION_ID + appType, session.getSessionId(), -1, request, response);
		CookieUtil.setCookie(ACCESS_TOKEN + appType, session.getAccessToken(), -1, request, response);
		// 添加到request
		request.setAttribute(SESSION_ID + appType, session);
		// 写入缓存
		ehcache.putSession(session.getSessionId(), session);
	}

	/**
	 * 删除Session
	 */
	public static void removeSession(String sessionId, HttpServletRequest request, HttpServletResponse response) {
		String appType = request.getParameter("appType");
		CookieUtil.setCookie(SESSION_ID + appType, null, 0, request, response);
		CookieUtil.setCookie(ACCESS_TOKEN + appType, null, 0, request, response);
		request.setAttribute(SESSION_ID + appType, null);
		ehcache.removeSession(sessionId);
	}

	/**
	 * 生成Session
	 */
	public static Session generateSession(String appType, String sessionId, String accessToken, Object user) {
		Session session = new Session();
		session.setSessionId(StringUtils.isBlank(sessionId)
				? MD5Utils.getMD5ofStr(SESSION_ID + System.currentTimeMillis()) : sessionId);
		session.setAccessToken(StringUtils.isBlank(accessToken) ? SignatureUtil.generateToke() : accessToken);
		session.setAppType(appType);
		session.setUser(user);
		session.setTs(System.currentTimeMillis());
		return session;
	}

}
