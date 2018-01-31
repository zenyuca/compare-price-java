package com.wzd.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * 资源工具类
 * 
 * @author WeiZiDong
 *
 */
public class Resource {

	private ClassLoader systemClassLoader;

	public Resource() {
		systemClassLoader = ClassLoader.getSystemClassLoader();
	}

	/**
	 * 获取实体类源代码的物理路径
	 * 
	 * @param clazz
	 * @return
	 */
	public static URL getCodeSourceURL(Class<?> clazz) {
		return clazz.getProtectionDomain().getCodeSource().getLocation();
	}

	/**
	 * 获取资源输入流
	 * 
	 * @param path
	 * @return
	 */
	public InputStream getResourceAsStream(String path) {
		// 返回读取指定资源的输入流
		return getResourceAsStream(path, getClassLoaders(null));
	}

	public InputStream getResourceAsStream(String resource, ClassLoader[] classLoader) {
		for (ClassLoader cl : classLoader) {
			if (null != cl) {

				// try to find the resource as passed
				InputStream returnValue = cl.getResourceAsStream(resource);

				// now, some class loaders want this leading "/", so we'll add
				// it and try again if we didn't find the resource
				if (null == returnValue)
					returnValue = cl.getResourceAsStream("/" + resource);

				if (null != returnValue)
					return returnValue;
			}
		}
		return null;
	}

	/**
	 * 获取资源输入流
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public Enumeration<URL> getResources(String path) throws IOException {
		// 返回读取指定资源的输入流
		return getResources(path, getClassLoaders(null));
	}

	public Enumeration<URL> getResources(String resource, ClassLoader[] classLoader) throws IOException {
		for (ClassLoader cl : classLoader) {
			if (null != cl) {
				Enumeration<URL> returnValue = cl.getResources(resource);
				// now, some class loaders want this leading "/", so we'll add
				// it and try again if we didn't find the resource
				if (null == returnValue)
					returnValue = cl.getResources("/" + resource);

				if (null != returnValue)
					return returnValue;
			}
		}
		return null;
	}

	private ClassLoader[] getClassLoaders(ClassLoader classLoader) {
		return new ClassLoader[] { classLoader, Thread.currentThread().getContextClassLoader(),
				getClass().getClassLoader(), systemClassLoader };
	}

}
