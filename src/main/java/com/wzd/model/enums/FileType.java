package com.wzd.model.enums;

import com.wzd.web.dto.exception.WebException;
import com.wzd.web.dto.response.ResponseCode;

/**
 * 文件类型
 * 
 */
public enum FileType {
	上传excel(1), 上传图片(2);
	private Integer value;

	private FileType(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public static FileType parse(Integer type) {
		for (FileType item : FileType.values()) {
			if (type != null && type.equals(item.getValue())) {
				return item;
			}
		}
		throw new WebException(ResponseCode.类型错误, "值[" + type + "]不是" + FileType.class + "有效值。");
	}
}
