package com.wzd.model.enums;

import com.wzd.web.dto.exception.WebException;
import com.wzd.web.dto.response.ResponseCode;

/**
 * 活动类型
 * 
 * @author WeiZiDong
 *
 */
public enum ActivityType {
	普通活动(0), 健身活动(1), 工会活动(2), 相亲活动(3), 红包活动(4), 优惠券活动(5), 电影票活动(6), 招聘活动(7), 内部会议(8), 内部活动(9);
	private Integer value;

	private ActivityType(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public static ActivityType parse(Integer type) {
		for (ActivityType item : ActivityType.values()) {
			if (type != null && type == item.getValue()) {
				return item;
			}
		}
		throw new WebException(ResponseCode.不允许此方法, "值[" + type + "]不是" + ActivityType.class + "有效值。");
	}
}