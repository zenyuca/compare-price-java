package com.wzd.model.dao;

import java.util.Date;
import java.util.List;

import com.wzd.model.enums.RoleType;
import com.wzd.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wzd.model.mapper.AdminMapper;
import com.wzd.web.dto.Admin;

import tk.mybatis.mapper.entity.Example;

@Component
public class AdminDao {
	@Autowired
	private AdminMapper mapper;

	public List<Admin> selectByExample(Example example){
		return mapper.selectByExample(example);
	}

	public Integer insertSelective(Admin admin){
		admin.setId(UUIDUtil.get());
		admin.setCreateTime(new Date());
		admin.setUpdateTime(new Date());
		admin.setRole(RoleType.代理商.getValue());
		return mapper.insertSelective(admin);
	}

	public Integer deleteByExample(Example example){
		return mapper.deleteByExample(example);
	}

	public Integer updateByExampleSelective(Admin admin,Example example){
		admin.setUpdateTime(new Date());
		return mapper.updateByExampleSelective(admin,example);
	}

	public Admin selectOne(Admin admin){
		return mapper.selectOne(admin);
	}
}
