package com.wzd.web.rest.api;

import com.github.pagehelper.PageInfo;
import com.wzd.model.entity.Files;
import com.wzd.model.enums.DeleteType;
import com.wzd.service.FileService;
import com.wzd.web.dto.session.SessionUtil;
import com.wzd.web.filter.log.RequestLog;
import com.wzd.web.filter.log.RequestLogType;
import com.wzd.web.param.IdListParam;
import com.wzd.web.param.PageParam;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 文件接口
 *
 */
@Path("/file")
@Produces(MediaType.APPLICATION_JSON)
public class FileApi {
	@Autowired
	private FileService service;

	/**
	 * 上传文件
	 */
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@RequestLog(RequestLogType.NOTSUPPORTED)
	public Files upload(FormDataMultiPart form, @Context HttpServletRequest request) {
		return service.upload(form, request);
	}

	/**
	 * 下载文件
	 */
	@GET
	@Path("/download/{id}")
	@Produces("application/x-download")
	@RequestLog(RequestLogType.NOTSUPPORTED)
	public String download(@PathParam("id") String id, @Context HttpServletResponse response, @Context HttpServletRequest request) {
		return service.download(id, response, SessionUtil.getSession(request));
	}

	/**
	 * 修改文件
	 */
	@POST
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void update(Files file) {
		service.update(file);
	}

	/**
	 * 查詢文件
	 */
	@POST
	@Path("/find")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public PageInfo<Files> find(PageParam pageParam) {
		return service.find(pageParam);
	}

	/**
	 * 删除文件
	 */
	@DELETE
	@Path("/delete/{id}/{del}")
	public void delete(@PathParam("id") String id, @PathParam("del") Integer del) {
		service.delete(id, DeleteType.parse(del));
	}

	/**
	 * 删除文件
	 */
	@DELETE
	@Path("/deleteUrl/{url}")
	public void deleteUrl(@PathParam("url") String url) throws UnsupportedEncodingException {
		service.deleteUrl(URLDecoder.decode(url, "UTF-8"));
	}

	/**
	 * 批量删除文件
	 */
	@POST
	@Path("/deleteAll")
	public void deleteAll(IdListParam<String> param) {
		service.delete(param);
	}

	/**
	 * 条件查询文件列表
	 */
	@POST
	@Path("/list/{del}")
	public PageInfo<Files> list(PageParam param, @PathParam("del") Integer del) {
		return service.list(param, DeleteType.parse(del));
	}

}
