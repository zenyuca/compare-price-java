//package com.wzd.web.filter.authority;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.ws.rs.container.ContainerRequestContext;
//import javax.ws.rs.container.ContainerRequestFilter;
//import javax.ws.rs.container.ResourceInfo;
//import javax.ws.rs.core.Context;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import com.wzd.web.dto.exception.WebException;
//import com.wzd.web.dto.response.ResponseCode;
//import com.wzd.web.dto.session.Session;
//import com.wzd.web.dto.session.SessionUtil;
//
///**
// * 权限检验过滤器
// *
// * @author WeiZiDong
// *
// */
//public class AuthorityFilter implements ContainerRequestFilter {
//	private static final Logger log = LogManager.getLogger(AuthorityFilter.class);
//	@Context
//	private HttpServletRequest request;
//	@Context
//	private ResourceInfo resourceInfo;
//
//	@Override
//	public void filter(ContainerRequestContext context) throws IOException {
//		// 获取接口允许的权限
//		Authority authority = resourceInfo.getResourceMethod().getAnnotation(Authority.class);
//		if (authority == null) {
//			return;
//		}
//		AuthType[] auths = authority.value();
//		// 默认为所有
//		if (auths == null || auths.length == 0) {
//			return;
//		}
//		// TODO 权限验证
//		log.debug("接口允许的权限==>" + Arrays.toString(auths));
//		// 获取用户本身具有的权限
//		Session session = SessionUtil.getSession(request);
//		if (session == null) {
//			throw new WebException(ResponseCode.禁止访问, "未登录");
//		}
//		@SuppressWarnings("unchecked")
//		Map<String, Object> user = (Map<String, Object>) session.getUser();
//		String uAu = (String) user.get("auth");
//		log.debug("用户具有的权限==>" + uAu);
//		// 判断用户是否具有该接口与允许的权限
//		for (AuthType auth : auths) {
//			if (uAu.indexOf(auth.getValue().toString()) != -1) {
//				return;
//			}
//		}
//		throw new WebException(ResponseCode.未授权, "权限不足");
//	}
//
//}
