package com.wzd.utils;

import java.net.URL;

import com.wzd.web.dto.session.Session;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * 缓存工具类
 * 
 * @author WeiZiDong
 *
 */
public class EhcacheUtil {
	private static final String path = "/ehcache.xml";
	public static final String LOGIN_SESSION = "loginSession";
	public static final String TOKEN = "token";

	private URL url;

	private CacheManager manager;

	private static EhcacheUtil ehCache;

	private EhcacheUtil(String path) {
		url = getClass().getResource(path);
		manager = CacheManager.create(url);
	}

	public static EhcacheUtil getInstance() {
		if (ehCache == null) {
			ehCache = new EhcacheUtil(path);
		}
		return ehCache;
	}

	public void put(String cacheName, String key, Object value) {
		Cache cache = manager.getCache(cacheName);
		Element element = new Element(key, value);
		cache.put(element);
	}

	public Object get(String cacheName, String key) {
		Cache cache = manager.getCache(cacheName);
		Element element = cache.get(key);
		return element == null ? null : element.getObjectValue();
	}

	public Cache get(String cacheName) {
		return manager.getCache(cacheName);
	}

	public void remove(String cacheName, String key) {
		Cache cache = manager.getCache(cacheName);
		cache.remove(key);
	}

	public Session getSession(String key) {
		return (Session) get(LOGIN_SESSION, key);
	}

	public void putSession(String key, Object value) {
		put(LOGIN_SESSION, key, value);
	}

	public void removeSession(String key) {
		remove(LOGIN_SESSION, key);
	}

	public void clear() {
		manager.getCache(LOGIN_SESSION).removeAll();
		manager.getCache(TOKEN).removeAll();
	}
}
