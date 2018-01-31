package com.wzd.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 加载系统配置
 * 
 * @author WeiZiDong
 *
 */
public class Configs {
	private static final Logger log = LogManager.getLogger(Configs.class);
	// 配置路径
	private static String PROPERTIES = "configs/jdbc.properties";
	// 所有配置
	private static Map<String, String> propMap = null;
	/**
	 * 版本号
	 */
	public static String version = "1.0.0";
	/**
	 * 本机域名
	 */
	public static String hostname = "127.0.0.1";
	/**
	 * 企业号唯一凭证
	 */
	public static String sCorpID = "";
	/**
	 * 企业号唯一凭证密钥
	 */
	public static String sSecret = "";
	/**
	 * 企业号EncodingAESKey用于消息体的加密，是AES密钥的Base64编码。
	 */
	public static String sEncodingAESKey = "";
	/**
	 * 企业号Token可由企业任意填写，用于生成签名。
	 */
	public static String sToken = "";
	/**
	 * 服务号原始ID
	 */
	public static String bId = "";
	/**
	 * 服务号唯一凭证
	 */
	public static String bAppid = "";
	/**
	 * 服务号唯一凭证密钥
	 */
	public static String bSecret = "";
	/**
	 * 服务号EncodingAESKey用于消息体的加密，是AES密钥的Base64编码。
	 */
	public static String bEncodingAESKey = "";
	/**
	 * 服务号Token可由企业任意填写，用于生成签名。
	 */
	public static String bToken = "";
	/**
	 * 微信支付分配的商户号
	 */
	public static String mchId = "";
	/**
	 * 企业号应用ID
	 */
	private static List<Integer> agentIds;

	static {
		try {
			propMap = PropertiesUtil.readPropertiesForMap(PROPERTIES);
			version = propMap.get("version");
			hostname = propMap.get("hostname");
			sCorpID = propMap.get("sCorpID");
			sSecret = propMap.get("sSecret");
			sEncodingAESKey = propMap.get("sEncodingAESKey");
			sToken = propMap.get("sToken");
			bId = propMap.get("bId");
			mchId = propMap.get("mch_id");
			bAppid = propMap.get("bAppid");
			bSecret = propMap.get("bSecret");
			bEncodingAESKey = propMap.get("bEncodingAESKey");
			bToken = propMap.get("bToken");
		} catch (IOException e) {
			throw new RuntimeException("加载服务器配置发生异常。", e);
		} finally {
			log.info("启动成功！版本：" + version);
		}
	}

	/**
	 * 获取相关配置
	 * 
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		return propMap.get(key);
	}

	public static List<Integer> getAgentIdS() {
		if (agentIds == null) {
			agentIds = Arrays.asList(propMap.get("agentid").split("\\|")).stream().map((agentid) -> StringUtils.isNotBlank(agentid) ? Integer.parseInt(agentid) : null)
					.collect(Collectors.toList());
		}
		return agentIds;
	}

	public static Integer getAgentId(Integer index) {
		return getAgentIdS().get(index);
	}

}
