package com.wzd.web.rest.api;

import com.wzd.service.AdminService;
import com.wzd.web.dto.Admin;
import com.wzd.web.dto.session.Session;
import com.wzd.web.dto.session.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * 用户接口
 *
 */
@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AmdinApi {
	@Autowired
	private AdminService service;

	/**
	 * 登录
	 */
	@POST
	@Path("/login")
	public Session login(Admin admin,@Context HttpServletRequest request, @Context HttpServletResponse response) {
		return service.login(admin,request,response);
	}

	/**
	 * 查询
	 */
	@GET
	@Path("/find/agent")
	public List<Admin> findAgent(@Context HttpServletRequest request) {
		return service.findAgent((Admin) SessionUtil.getUser(request));
	}

	/**
	 * 新增
	 */
	@POST
	@Path("/add/agent")
	public void addAgent(Admin admin,@Context HttpServletRequest request) {
		service.addAgent(admin,(Admin) SessionUtil.getUser(request));
	}

	/**
	 * 删除
	 */
	@DELETE
	@Path("/del/agent/{id}")
	public void delAgent(@PathParam("id")String id,@Context HttpServletRequest request) {
		service.delAgent(id,(Admin) SessionUtil.getUser(request));
	}

	@PUT
	@Path("/update/agent")
	public void updateAgent(Admin admin,@Context HttpServletRequest request) {
		service.updateAgent(admin,(Admin) SessionUtil.getUser(request));
	}
}
