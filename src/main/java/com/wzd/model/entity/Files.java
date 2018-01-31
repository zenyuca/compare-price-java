package com.wzd.model.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.wzd.model.enums.DeleteType;
import com.wzd.model.enums.FileType;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * 文件
 * 
 * @author WeiZiDong
 *
 */
@SuppressWarnings("serial")
public class Files implements Serializable {
	@Id
	private String id; // ID
	// 自有属性
	private String name; // 文件名
	private String url; // url地址
	@JSONField(serialize = false)
	private String fk; // 外键
	private String suffix; // 后缀名
	@Column(name = "user_id")
	private String userId; // 上传者
	private Integer num; // 下载次数
	private Date created; // 上传时间
	private Integer deleted; // 删除标志
	private Integer status; // 状态
	private Integer type; // 类型
	private String description;// 文件说明

	@Transient
	private String searchText;
	@Transient
	private Integer searchType;

	public Files() {
		super();
	}

	public Files(String fk, FileType type, String description) {
		this.fk = fk;
		this.type = type.getValue();
		this.description = description;
	}

	public Files(String fk, FileType type) {
		this.fk = fk;
		this.type = type.getValue();
	}

	public Files(String url) {
		this.url = url;
	}

	public Files(String id, String fk, Integer type) {
		this.id = id;
		this.fk = fk;
		this.type = type;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public Integer getSearchType() {
		return searchType;
	}

	public void setSearchType(Integer searchType) {
		this.searchType = searchType;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFk() {
		return fk;
	}

	public void setFk(String fk) {
		this.fk = fk;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

	public void setDeleted(DeleteType del) {
		this.deleted = del.getValue();
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}


	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setType(FileType type) {
		this.type = type.getValue();
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}