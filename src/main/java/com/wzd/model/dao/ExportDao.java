package com.wzd.model.dao;

import com.wzd.model.enums.DeleteType;
import com.wzd.model.mapper.ExportMapper;
import com.wzd.utils.UUIDUtil;
import com.wzd.web.dto.Admin;
import com.wzd.web.dto.Export;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Component
public class ExportDao {
    @Autowired
    private ExportMapper mapper;

    public List<Export> selectByExample(Example example) {
        return mapper.selectByExample(example);
    }

    public void create(Export export) {
        export.setId(UUIDUtil.get());
        export.setCreateTime(new Date());
        export.setUpdateTime(new Date());
        export.setDeleted(DeleteType.未删除.getValue());
        mapper.insert(export);
    }

    public Export selectOne(Export export) {
        return mapper.selectOne(export);
    }

}
