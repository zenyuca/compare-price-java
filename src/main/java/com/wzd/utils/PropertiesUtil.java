package com.wzd.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.client.Client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 读取properties文件工具
 * 
 * @author WeiZiDong
 *
 */
public class PropertiesUtil {
	private static final Logger log = LogManager.getLogger(PropertiesUtil.class);

	/**
	 * 读取properties的全部信息
	 * 
	 * @param filePath
	 */
	public static Properties readProperties(String path) throws IOException {

		Properties props = new Properties();

		try {

			InputStream in = new Resource().getResourceAsStream(path);
			props.load(in);

		} catch (IOException e) {
			throw new IOException("读取[" + path + "]properties文件错误", e);
		}
		return props;
	}

	/**
	 * 读取properties的全部信息
	 * 
	 * @param filePath
	 */
	public static Map<String, String> readPropertiesForMap(String path) throws IOException {

		Properties props = new Properties();
		Map<String, String> propsMap = new HashMap<String, String>();

		try {
			InputStreamReader in = new InputStreamReader(Client.class.getClassLoader().getResourceAsStream(path), "UTF-8");
			props.load(in);
			Enumeration<?> en = props.propertyNames();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String property = props.getProperty(key);
				propsMap.put(key, property);
			}

		} catch (IOException e) {
			throw new IOException("读取[" + path + "]properties文件错误", e);
		}
		return propsMap;
	}

	/**
	 * 根据key读取value
	 * 
	 * @param filePath
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public static String readValue(String filePath, String key) throws IOException {
		try {
			Properties props = readProperties(filePath);
			String value = props.getProperty(key);
			log.debug("读取配置【" + key + "】成功: " + value);
			return value;
		} catch (IOException e) {
			throw new IOException("读取[" + filePath + "]properties文件[" + key + "]错误", e);
		}
	}

	// public static <T> T readPropertiesBean(Class<T> beanClass)
	// throws IOException, IllegalAccessException, IllegalArgumentException,
	// InvocationTargetException,
	// NoSuchMethodException, SecurityException, NoSuchFieldException {
	// // class注解
	// Property filePathAnnotation = beanClass.getAnnotation(Property.class);
	//
	// if (filePathAnnotation == null) {
	//
	// throw new IOException("[" + beanClass + "]class不存在Property注解");
	// }
	//
	// // class注解值
	// String filePath = filePathAnnotation.value();
	//
	// if (StringUtil.isEmpty(filePath)) {
	//
	// throw new IOException("[" + beanClass + "]Property注解值为空");
	// }
	//
	// // 根据class注解获取属性文件
	// Properties props = readProperties(filePath);
	//
	// // beanClass字段
	// Field[] fields = beanClass.getDeclaredFields();
	//
	// T bean;
	// try {
	// bean = beanClass.newInstance();
	// } catch (InstantiationException | IllegalAccessException e) {
	// throw new IOException("实例化[" + beanClass + "]错误", e);
	// }
	//
	// // 迭代beanClass字段，获取注解值，赋值
	// for (int i = 0; i < fields.length; i++) {
	// Field field = fields[i];
	//
	// // 获取字段注解
	// Property fieldProperty = field.getAnnotation(Property.class);
	//
	// // 对应字段属性名
	// String propName = null;
	//
	// if (fieldProperty == null) {
	// propName = field.getName();
	// } else {
	// propName = fieldProperty.value();
	// }
	//
	// // // 对应字段属性名
	// // propName = fieldProperty.value();
	//
	// if (StringUtil.isEmpty(propName)) {
	// continue;
	// }
	//
	// String propValue = props.getProperty(propName);
	//
	// ReflectUtil.invokeSetMedhod(bean, propName, propValue);
	// }
	//
	// return bean;
	// }

}
