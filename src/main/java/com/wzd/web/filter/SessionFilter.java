package com.wzd.web.filter;

import com.wzd.web.dto.session.Session;
import com.wzd.web.dto.session.SessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Session校验
 * 
 * @author WeiZiDong
 *
 */
@Component("SessionFilter")
public class SessionFilter implements Filter {
	
	private static final Logger log = LogManager.getLogger(SessionFilter.class);
	@Autowired
	private SessionUtil sessionUtil;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		// 请求
		String requestUrl = httpRequest.getRequestURI().substring(1);
		// 回调授权code
		String code = request.getParameter("code");
		// agentid
		String agentid = request.getParameter("agentid");
		// 微信回调不检测
		if (requestUrl.startsWith("rest/wechat/") || requestUrl.contains("getToken")) {
			chain.doFilter(httpRequest, httpResponse);
			return;
		}
		if (httpRequest.getQueryString() != null) {
			requestUrl += "?" + httpRequest.getQueryString();
		}
		log.debug("请求：" + requestUrl);
		log.debug("code：" + code);
		Session session = null;
		// debug模式
		if (SessionUtil.isDebug(httpRequest)) {
			log.debug("开启debug模式！");
			session = sessionUtil.openDebug(httpRequest);
		} else {
			session = SessionUtil.getSession(httpRequest);
		}

		// 加载静态文件
		if (StringUtils.isBlank(requestUrl) || requestUrl.startsWith("view/") || requestUrl.startsWith("login")) {
			log.debug("加载静态页面。。。");
			request.getRequestDispatcher("/index.html?v=" + System.currentTimeMillis()).forward(request, response);
			return;
		}
		// 更新Session
		if (session != null) {
			SessionUtil.updateSession(session, httpRequest);
		}
		chain.doFilter(httpRequest, httpResponse);
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
