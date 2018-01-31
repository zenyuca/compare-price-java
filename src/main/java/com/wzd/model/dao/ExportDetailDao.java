package com.wzd.model.dao;

import com.wzd.model.mapper.ExportDetailMapper;
import com.wzd.model.mapper.ExportMapper;
import com.wzd.utils.UUIDUtil;
import com.wzd.web.dto.Export;
import com.wzd.web.dto.ExportDetail;
import com.wzd.web.dto.ExportDetailCopy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class ExportDetailDao {
	@Autowired
	private ExportDetailMapper mapper;

	public List<ExportDetail> selectByExample(Example example){
		return mapper.selectByExample(example);
	}

	public void createList(List<ExportDetail> details){
		details.stream().forEach(d ->{
			d.setId(UUIDUtil.get());
		});
		mapper.insertList(details);
	}

	public int deleteByExample(Example example){
		return mapper.deleteByExample(example);
	}

	public List<ExportDetail> findTenderResult(String exportId){
		return mapper.findTenderResult(exportId);
	}
}
