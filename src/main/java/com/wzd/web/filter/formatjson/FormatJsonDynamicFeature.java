package com.wzd.web.filter.formatjson;

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
public class FormatJsonDynamicFeature implements DynamicFeature {

	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
		// 获取资源方法
		Method resourceMethod = resourceInfo.getResourceMethod();
		if (resourceMethod != null) {
			// 获取FormatJson注解
			FormatJson formatJson = resourceMethod.getAnnotation(FormatJson.class);
			// 类型
			FormatJsonType formatJsonType = null;
			// 注解为空默认为需要格式化json
			if (formatJson == null) {
				formatJsonType = FormatJsonType.REQUIRED;
			} else {
				// 获取注解的值
				formatJsonType = formatJson.value();
			}
			// 需要格式化
			if (formatJsonType != FormatJsonType.NOTSUPPORTED) {
				context.register(FormatJsonResponseFilter.class);
			}
		}
	}
}
