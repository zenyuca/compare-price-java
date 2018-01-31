package com.wzd.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wzd.model.dao.FileDao;
import com.wzd.model.entity.Files;
import com.wzd.model.enums.DeleteType;
import com.wzd.model.enums.FileType;
import com.wzd.utils.FileUtil;
import com.wzd.web.dto.exception.WebException;
import com.wzd.web.dto.response.ResponseCode;
import com.wzd.web.dto.session.Session;
import com.wzd.web.param.IdListParam;
import com.wzd.web.param.PageParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * 文件上传
 */
@Service
public class FileService {
    private static final Logger log = LogManager.getLogger(FileService.class);
    @Autowired
    private FileDao dao;

    /**
     * 上传文件
     */
    public Files upload(FormDataMultiPart form, HttpServletRequest request) {
        log.debug("开始上传文件。。。");
        FormDataBodyPart filePart = form.getField("file");
//		Integer type = null;
//		if (form.getField("type") != null) {
//			type = form.getField("type").getValueAs(Integer.class);
//		}
        InputStream file = filePart.getValueAs(InputStream.class);
        FormDataContentDisposition disposition = filePart.getFormDataContentDisposition();
        Files f = FileUtil.writeFile(file, disposition);
        f.setType(FileType.上传图片.getValue());
        f.setDeleted(DeleteType.永久删除.getValue());
        dao.create(f);
        log.debug("上传文件成功:" + f);
        return f;
    }

    public void update(Files files) {
        files.setDeleted(DeleteType.未删除);
        dao.update(files);
    }

    public PageInfo<Files> find(PageParam pageParam) {
        Example example = new Example(Files.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", FileType.上传图片.getValue())
                .andEqualTo("deleted", DeleteType.未删除.getValue());
        if (pageParam.getKeyParam() != null) {
            Files files = JSON.parseObject(JSON.toJSONString(pageParam.getKeyParam()), Files.class);
            if (files.getSearchType() == 1) {
                criteria.andLike("name","%"+files.getSearchText()+"%");
            }
        }
        PageHelper.startPage(pageParam.getPage(), pageParam.getPageSize());
        return new PageInfo<>(dao.selectByExample(example));
    }

    /**
     * 删除文件
     */
    public void delete(String id, DeleteType del) {
        Files f = dao.getById(id);
        if (f == null) {
            throw new WebException(ResponseCode.文件不存在);
        }
        if (DeleteType.永久删除 != del) {
            f.setDeleted(del);
            dao.update(f);
        } else {
            dao.delete(id);
            FileUtil.delete(f.getUrl());
        }
    }

    /**
     * 获取文件列表
     */
    public PageInfo<Files> list(PageParam param, DeleteType del) {
        return dao.list(param, del);
    }

    /**
     * 批量删除文件
     */
    public void delete(IdListParam<String> param) {
        DeleteType type = DeleteType.parse(param.getType());
        param.getIds().forEach(id -> {
            delete(id, type);
        });
    }

    /**
     * 查询没有使用的文件
     */
    public List<Files> getNotFkFiles() {
        return dao.getNotFk();
    }

    /**
     * 下载文件
     */
    public String download(String id, HttpServletResponse response, Session session) {
        Files file = dao.getById(id);
        if (file == null) {
            return ResponseCode.文件不存在.name();
        }
//		if (APPType.管理平台.getValue().equals(session.getAppType()) || APPType.企业号.getValue().equals(session.getAppType())) {
//			Admin a = (Admin) session.getUser();
//			historyDao.create(new History(a.getId(), "下载文件", null, 0, null, HistoryType.文件下载, id));
//		} else {
//			User u = (User) session.getUser();
//			if (u != null) {
//				historyDao.create(new History(u.getId(), "下载文件", null, 0, null, HistoryType.文件下载, id));
//			}
//		}
        file.setNum(file.getNum() != null ? file.getNum() + 1 : 1);
        dao.update(file);
        FileUtil.download(response, file);
        return ResponseCode.成功.name();
    }

    /**
     * 删除文件
     */
    public void deleteUrl(String url) {
        Files f = dao.getByUrl(url);
        if (f == null) {
            throw new WebException(ResponseCode.文件不存在);
        }
        dao.delete(f.getId());
        FileUtil.delete(url);
    }

}
