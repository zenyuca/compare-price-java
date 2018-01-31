package com.wzd.web.rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.web.filter.RequestContextFilter;

import com.alibaba.fastjson.support.jaxrs.FastJsonProvider;
import com.wzd.web.dto.exception.BaseExceptionMapper;
import com.wzd.web.filter.formatjson.FormatJsonDynamicFeature;
import com.wzd.web.filter.log.RequestLogDynamicFeature;

/**
 * 接口注册
 * 
 * @author WeiZiDong
 *
 */
@ApplicationPath("/rest")
public class ApiConfig extends ResourceConfig {
	public ApiConfig() {
		this.packages(this.getClass().getPackage().getName());
		// 用 Jackson JSON 的提供者来解释 JSON
		register(FastJsonProvider.class);
		// Spring filter 提供了 JAX-RS 和 Spring 请求属性之间的桥梁
		register(RequestContextFilter.class);
		// 注册请求日志过滤器
		register(RequestLogDynamicFeature.class);
		// 将返回值转换为JsonResponse
		register(FormatJsonDynamicFeature.class);
		// 注册异常转换
		register(BaseExceptionMapper.class);
		// 文件上传
		register(MultiPartFeature.class);
		// 接口校验
//		register(AuthorityFilter.class);
	}
}
