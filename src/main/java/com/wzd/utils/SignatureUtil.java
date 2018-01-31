package com.wzd.utils;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;

/**
 * 数字签名工具类
 * 
 * @author WeiZiDong
 * 
 */
public class SignatureUtil {

	/**
	 * 数字签名是否合法
	 * 
	 * @param token
	 *            访问令牌
	 * @param signature
	 *            数字签名
	 * @param timestamp
	 *            时间戳
	 * @param nonce
	 *            随机数
	 * @return
	 */
	public static boolean checkSignature(String token, String signature, String timestamp, String nonce) {
		if (StringUtils.isBlank(token) || StringUtils.isBlank(signature) || StringUtils.isBlank(timestamp)
				|| StringUtils.isBlank(nonce)) {
			return false;
		}
		// 这里的token是个人端登录或注册用户时生成，在需要验证登录的接口中传入此token
		String[] tmpArr = { token, timestamp, nonce };
		// 将token、timestamp、nonce三个参数进行字典序排序
		Arrays.sort(tmpArr);
		// 将三个参数字符串拼接成一个字符串
		String tmpStr = ArrayToString(tmpArr);
		// 进行sha1加密
		tmpStr = SHA1Encode(tmpStr);
		return StringUtils.equalsIgnoreCase(tmpStr, signature);
	}

	/**
	 * 生成数字签名
	 * 
	 * @param token
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	public static String generateSignature(String token, String timestamp, String nonce) {
		// 这里的token是个人端登录或注册用户时生成，在需要验证登录的接口中传入此token
		String[] tmpArr = { token, timestamp, nonce };

		// 将token、timestamp、nonce三个参数进行字典序排序
		Arrays.sort(tmpArr);

		// 将三个参数字符串拼接成一个字符串
		String tmpStr = ArrayToString(tmpArr);

		tmpStr = SHA1Encode(tmpStr);

		return tmpStr;
	}

	// 数组转字符串
	public static String ArrayToString(String[] arr) {
		StringBuffer bf = new StringBuffer();
		for (String str : arr) {
			bf.append(str);
		}
		return bf.toString();
	}

	// 进行sha1加密
	public static String SHA1Encode(String sourceString) {
		String resultString = null;
		try {
			resultString = new String(sourceString);
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			resultString = byte2hexString(md.digest(resultString.getBytes()));
		} catch (Exception ex) {
		}
		return resultString;
	}

	public static String byte2hexString(byte[] bytes) {
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			if (((int) bytes[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) bytes[i] & 0xff, 16));
		}
		return buf.toString().toLowerCase();
	}

	/**
	 * 生成令牌
	 * 
	 * @return
	 */
	public static String generateToke() {

		String value = String.valueOf(System.currentTimeMillis());

		value += UUIDUtil.get();

		value += String.valueOf(new Random().nextInt(1000));

		try {

			MessageDigest md = MessageDigest.getInstance("md5");

			byte[] b = md.digest(value.getBytes()); // 产生数据的指纹

			// Base64编码
			return Base64Utils.encodeToString(b); // 生成token

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
