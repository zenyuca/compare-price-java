package com.wzd.service;

import com.wzd.model.enums.RoleType;
import com.wzd.model.mapper.ConfigureMapper;
import com.wzd.model.mapper.RemarkMapper;
import com.wzd.utils.UUIDUtil;
import com.wzd.web.dto.Configure;
import com.wzd.web.dto.Remark;
import com.wzd.web.dto.exception.WebException;
import com.wzd.web.dto.response.ResponseCode;
import com.wzd.web.dto.session.Session;
import com.wzd.web.dto.session.SessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wzd.model.dao.AdminDao;
import com.wzd.web.dto.Admin;
import com.wzd.web.param.PageParam;

import tk.mybatis.mapper.entity.Example;

import javax.management.relation.Role;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AdminService {
    @Autowired
    private AdminDao adminDao;
    @Autowired
    private RemarkMapper remarkMapper;
    @Autowired
    private ConfigureMapper configureMapper;

    public Session login(Admin admin, HttpServletRequest request, HttpServletResponse response) {
        Admin dbAdmin = new Admin();
        dbAdmin.setName(admin.getName());
        dbAdmin = adminDao.selectOne(dbAdmin);
        if (dbAdmin == null) {
            throw new WebException(ResponseCode.用户不存在);
        }
        String pwd = admin.getPwd();
        if (StringUtils.isBlank(pwd) || !pwd.equals(dbAdmin.getPwd())) {
            throw new WebException(ResponseCode.密码错误);
        }
        // 保存Session
        Session session = SessionUtil.generateSession(null, dbAdmin.getId(), null, dbAdmin);
        SessionUtil.saveSession(session, request, response);
        return session;
    }

    public List<Admin> findAgent(Admin admin) {
//        if (!RoleType.管理员.getValue().equals(admin.getRole()))
//            throw new WebException(ResponseCode.当前登录人权限不足);
        Example example = new Example(Admin.class);
        return adminDao.selectByExample(example);
    }

    public void addAgent(Admin admin, Admin a) {
        if (!RoleType.管理员.getValue().equals(a.getRole()))
            throw new WebException(ResponseCode.当前登录人权限不足);
        adminDao.insertSelective(admin);
    }

    public void delAgent(String id, Admin admin) {
        if (!RoleType.管理员.getValue().equals(admin.getRole()))
            throw new WebException(ResponseCode.当前登录人权限不足);
        if (StringUtils.isBlank(id)) throw new WebException(ResponseCode.数据参数异常);
        Example example = new Example(Admin.class);
        example.createCriteria().andEqualTo("id", id);
        adminDao.deleteByExample(example);
    }

    public void updateAgent(Admin admin, Admin a) {
        if (!RoleType.管理员.getValue().equals(a.getRole()))
            throw new WebException(ResponseCode.当前登录人权限不足);
        if (StringUtils.isBlank(admin.getId())) throw new WebException(ResponseCode.数据参数异常);
        Example example = new Example(Admin.class);
        example.createCriteria().andEqualTo("id", admin.getId());
        adminDao.updateByExampleSelective(admin, example);
    }

    public void addRemark(Remark remark, Admin admin) {
        remark.setId(UUIDUtil.get());
        remark.setCreateTime(new Date());
        remarkMapper.insertSelective(remark);
    }

    public List<Remark> findRemarkById(String id, Admin admin) {
        Example example = new Example(Remark.class);
        example.setOrderByClause("createTime DESC");
        example.createCriteria().andEqualTo("agentId", id);
        return remarkMapper.selectByExample(example);
    }

    public void delRemarkById(String id, Admin admin) {
        Example example = new Example(Remark.class);
        example.createCriteria().andEqualTo("id", id);
        remarkMapper.deleteByExample(example);
    }

    public void addConfigure(Configure configure, Admin admin) {
        Example example = new Example(Configure.class);
        example.createCriteria().andEqualTo("type", configure.getType());
        Integer i = configureMapper.updateByExampleSelective(configure, example);
        if (i < 1) {
            configure.setId(UUIDUtil.get());
            configureMapper.insertSelective(configure);
        }
    }

    public Configure getConfigure(Integer type, Admin admin) {
        Configure configure = new Configure();
        configure.setType(type);
        return configureMapper.selectOne(configure);
    }
}
