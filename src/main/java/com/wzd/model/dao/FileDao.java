package com.wzd.model.dao;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wzd.model.entity.Files;
import com.wzd.model.enums.DeleteType;
import com.wzd.model.enums.FileType;
import com.wzd.model.mapper.FilesMapper;
import com.wzd.utils.UUIDUtil;
import com.wzd.web.param.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.Date;
import java.util.List;

/**
 * 文件数据库操作
 *
 */
@Component
public class FileDao {
	@Autowired
	private FilesMapper mapper;

	/**
	 * 创建
	 */
	public Files create(Files file) {
		file.setId(UUIDUtil.get());
		file.setCreated(new Date());
		file.setDeleted(DeleteType.未删除);
//		file.setStatus(StateType.进行中);
		mapper.insertSelective(file);
		return file;
	}

	/**
	 * 物理删除
	 */
	public void delete(String id) {
		Files f = new Files();
		f.setId(id);
		mapper.delete(f);
	}

	/**
	 * 根据ID查找文件
	 */
	public Files getById(String id) {
		Files f = new Files();
		f.setId(id);
		return mapper.selectOne(f);
	}

	/**
	 * 根据URL查找文件
	 */
	public Files getByUrl(String url) {
		Files f = new Files();
		f.setUrl(url);
		return mapper.selectOne(f);
	}

	/**
	 * 获取文件列表
	 */
	public PageInfo<Files> list(PageParam param, DeleteType del) {
		Example e = new Example(Files.class);
		Criteria c = PageParam.setCondition(e, "created", param, Files.class);
		if (del != DeleteType.全部) {
			c.andEqualTo("deleted", del.getValue());
		}
		PageParam.setOrderByClause(e, "created DESC");
		PageHelper.startPage(param.getPage(), param.getPageSize());
		return new PageInfo<Files>(mapper.selectByExample(e));
	}

	/**
	 * 修改
	 */
	public void update(Files f) {
		mapper.updateByPrimaryKeySelective(f);
	}

	public void update(Files f, FileType type, String fk) {
		Example e = new Example(Files.class);
		e.createCriteria().andEqualTo(f);
		mapper.updateByExampleSelective(new Files(fk, type), e);
	}

	/**
	 * 修改
	 */
	public void update(List<Files> files, FileType type, String fk) {
		if (files != null && files.size() > 0) {
			files.forEach((f) -> {
				f.setFk(fk);
				f.setType(type);
				mapper.updateByPrimaryKeySelective(f);
			});
		}
	}

	/**
	 * 批量更新
	 */
	public void update(Files f, List<String> ids) {
		Example e = new Example(Files.class);
		e.createCriteria().andIn("id", ids);
		mapper.updateByExampleSelective(f, e);
	}

	/**
	 * 根据外键查询
	 */
	public List<Files> getByFk(Files files) {
		Example e = new Example(Files.class);
		e.setOrderByClause("created ASC");
		e.createCriteria().andEqualTo(files);
		return mapper.selectByExample(e);
	}

	/**
	 * 根据外键删除
	 */
	public void deleteByFk(String fk, DeleteType del) {
		Files f = new Files();
		f.setFk(fk);
		if (del == DeleteType.永久删除) {
			mapper.delete(f);
		} else {
			f.setDeleted(del);
			update(f);
		}
	}

	/**
	 * 获取没有外键关联的文件
	 */
	public List<Files> getNotFk() {
		Example e = new Example(Files.class);
		e.createCriteria().andIsNull("fk");
		return mapper.selectByExample(e);
	}

	public List<Files> selectByExample(Example e) {
		return mapper.selectByExample(e);
	}
}
