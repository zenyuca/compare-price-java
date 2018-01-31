package com.wzd.model.enums;

import com.wzd.web.dto.exception.WebException;
import com.wzd.web.dto.response.ResponseCode;

/**
 * 角色类型
 */
public enum RoleType {
	代理商(0), 管理员(1);
	private Integer value;

	private RoleType(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public static RoleType parse(Integer type) {
		for (RoleType item : RoleType.values()) {
			if (type != null && type == item.getValue()) {
				return item;
			}
		}
		throw new WebException(ResponseCode.不允许此方法, "值[" + type + "]不是" + RoleType.class + "有效值。");
	}
}