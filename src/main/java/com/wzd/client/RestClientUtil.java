package com.wzd.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.glassfish.jersey.client.ClientConfig;

import com.alibaba.fastjson.JSON;
import com.wzd.client.filter.log.LogRequestFilter;
import com.wzd.client.filter.log.LogResponseFilter;

/**
 * 客户端Client
 *
 */
public class RestClientUtil {

	private static Client baseClient;

	static {
		buildClients();
	}

	private static void buildClients() {
		ClientConfig clientConfig = new ClientConfig();
		// 注册JacksonJsonProvider
		clientConfig.register(JacksonJsonProvider.class);
		clientConfig.register(LogRequestFilter.class);
		clientConfig.register(LogResponseFilter.class);
		baseClient = ClientBuilder.newClient(clientConfig);
	}

	public static Client getBaseClient() {
		return baseClient;
	}

	public static WebTarget buildWebTarget(String url) {
		return baseClient.target(url);
	}

	public static <T> T postForm(String path, MultivaluedMap<String, String> formParam, Class<T> beanClass) {
		Form form = new Form(formParam);
		String json = buildWebTarget(path).request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
		return JSON.parseObject(json, beanClass);
	}

	public static <T> T postJson(String path, Object param, Class<T> beanClass) {
		String json = buildWebTarget(path).request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(param, MediaType.APPLICATION_JSON_TYPE), String.class);
		return JSON.parseObject(json, beanClass);
	}

	public static <T> T get(String path, Class<T> beanClass) {
		String json = buildWebTarget(path).request().get(String.class);
		return JSON.parseObject(json, beanClass);
	}

}
