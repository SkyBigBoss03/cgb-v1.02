package com.jt.sys.controller;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.jt.sys.entity.SysUser;
import com.jt.sys.service.SysUserService;
import com.jt.sys.vo.JsonResult;

@Controller
@RequestMapping("/user/")
public class SysUserController {

	@Autowired
	private SysUserService sysUserService;
	
	@RequestMapping("listUI")
	public String listUI() {
		return "sys/user_list";
	}
	
	@RequestMapping("pageUI")
	public String pageUI() {
		return "common/page";
	}
	
	@RequestMapping("editUI")
	public String editUI() {
		return "sys/user_edit";
	}	
	
	@RequestMapping("doFindPageObjects")
	@ResponseBody
	public JsonResult doFindPageObjects(Integer currentPage, String username) {
		PageInfo<SysUser> pageObject = this.sysUserService.findPageObjects(currentPage, username);
		return new JsonResult(pageObject, "query ok");
	}
	
	@RequestMapping("doValidById")
	@ResponseBody
	public JsonResult doValidById(Integer id, Integer valid, String modifiedUser) {
		this.sysUserService.validById(id, valid, modifiedUser);
		return new JsonResult("update ok");
	}
	
	@RequestMapping("doSaveObject")
	@ResponseBody
	public JsonResult doSaveObject(SysUser sysUser, String[] roleIds) {
		this.sysUserService.saveObject(sysUser, roleIds);
		return new JsonResult("save ok");
	}
	
	@RequestMapping("doFindObjectById")
	@ResponseBody
	public JsonResult doFindObjectById(Integer id) {
		Map<String, Object> map = this.sysUserService.findObjectById(id);
		return new JsonResult(map, "query ok");
	}
	
	@RequestMapping("doUpdateObject")
	@ResponseBody
	public JsonResult doUpdateObject(SysUser sysUser, String[] roleIds) {
		this.sysUserService.updateObject(sysUser, roleIds);
		return new JsonResult("update ok");
	}
	
	@RequestMapping("doExportExcel")
	public void doExportExcelUsers(HttpServletResponse response) {
		this.sysUserService.exportExcelUsers(response);;
	}
	
	@RequestMapping("doExportPDF")
	public void doExportPDFUsers(HttpServletResponse response) {
		this.sysUserService.exportPDFUsers(response);
	}
}
