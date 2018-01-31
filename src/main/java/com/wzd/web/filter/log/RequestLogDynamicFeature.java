package com.wzd.web.filter.log;

import java.lang.reflect.Method;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

/**
 * 动态绑定格式化响应对象
 * 
 * @author WeiZiDong
 *
 */
public class RequestLogDynamicFeature implements DynamicFeature {

	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
		// 获取资源方法
		Method resourceMethod = resourceInfo.getResourceMethod();

		if (resourceMethod != null) {

			// 获取RequestLog注解
			RequestLog requestLogAnn = resourceMethod.getAnnotation(RequestLog.class);

			// 类型
			RequestLogType requestLogType = null;

			// 注解为空默认为需要日志
			if (requestLogAnn == null) {
				requestLogType = RequestLogType.REQUIRED;
			} else {
				// 获取注解的值
				requestLogType = requestLogAnn.value();
			}

			// 需要日志
			if (requestLogType != RequestLogType.NOTSUPPORTED) {
				context.register(RequestLogFilter.class);
			}

		}
	}
}
