package com.wzd.model.enums;

import com.wzd.web.dto.exception.WebException;
import com.wzd.web.dto.response.ResponseCode;

/**
 * 删除状态
 * 
 */
public enum DeleteType {
	全部(-1), 未删除(0), 回收站(1), 永久删除(2);
	private Integer value;

	private DeleteType(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public static DeleteType parse(Integer type) {
		for (DeleteType item : DeleteType.values()) {
			if (type != null && type == item.getValue()) {
				return item;
			}
		}
		throw new WebException(ResponseCode.不允许此方法, "值[" + type + "]不是" + DeleteType.class + "有效值。");
	}
}
