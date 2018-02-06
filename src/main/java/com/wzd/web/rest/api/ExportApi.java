package com.wzd.web.rest.api;

import com.github.pagehelper.PageInfo;
import com.wzd.service.AdminService;
import com.wzd.service.ExportService;
import com.wzd.web.dto.Admin;
import com.wzd.web.dto.Export;
import com.wzd.web.dto.ExportDetail;
import com.wzd.web.dto.ExportDetailCopy;
import com.wzd.web.filter.log.RequestLog;
import com.wzd.web.filter.log.RequestLogType;
import com.wzd.web.param.PageParam;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * 导入数据接口
 */
@Path("/export")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExportApi {
	@Autowired
	private ExportService service;

	@POST
	@Path("/find/{type}")
	public Export find(@PathParam("type")Integer type) {
		return service.find(type);
	}

	/**
	 * 管理员上传文件招标模板文件
	 */
	@POST
	@Path("/import/excel")
	public Object importExcel(Export export, @Context HttpServletRequest request) {
		return service.importExcel(export, request);
	}

	/**
	 * 上传招标文件
	 */
	@POST
	@Path("/import/tender/excel/{type}/{exportId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@RequestLog(RequestLogType.NOTSUPPORTED)
	public Object importTenderExcel(@PathParam("type")Integer type,@PathParam("exportId")String exportId,
									FormDataMultiPart form, @Context HttpServletRequest request) {
		return service.importTenderExcel(type,exportId,form, request);
	}

	/**
	 * 获取招标列表 查询最新招标列表
	 */
	@GET
	@Path("/find/bj_export_detail_copy/{type}")
	public Map findDetailCopyByType(@PathParam("type")Integer type, @Context HttpServletRequest request) {
		return service.findDetailCopyByType(type);
	}

	/**
	 * 根据类型查询投标结果
	 */
	@POST
	@Path("/find/tender/result/{type}")
	public List<ExportDetail> findTenderResult(@PathParam("type")Integer type, String agentId) {
		return service.findTenderResult(type,agentId);
	}

	/**
	 * 根据类型导出投标结果
	 */
	@POST
	@Path("/export/tender/result/{type}")
	public String exportTenderResult(@PathParam("type")Integer type, String agentId) {
		return service.exportTenderResult(type,agentId);
	}

}
