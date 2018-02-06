package com.wzd.model.mapper;


import com.wzd.web.dto.ExportDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExportDetailMapper extends MyMapper<ExportDetail> {

    public List<ExportDetail> findTenderResult(@Param("exportId") String exportId, @Param("agentId") String agentId);
}