package com.wzd.model.dao;

import com.wzd.model.mapper.ExportDetailCopyMapper;
import com.wzd.utils.UUIDUtil;
import com.wzd.web.dto.ExportDetailCopy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class ExportDetailCopyDao {
	@Autowired
	private ExportDetailCopyMapper mapper;

	public List<ExportDetailCopy> selectByExample(Example example){
		return mapper.selectByExample(example);
	}

	public void createList(List<ExportDetailCopy> detailCopies){
		detailCopies.stream().forEach(d ->{
			d.setId(UUIDUtil.get());
		});
		mapper.insertList(detailCopies);
	}
}
