package com.jt.sys.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.jt.sys.entity.SysRole;
import com.jt.sys.service.SysRoleService;
import com.jt.sys.vo.CheckBox;
import com.jt.sys.vo.JsonResult;

@Controller
@RequestMapping("/role/")
public class SysRoleController {
	
	@Autowired
	private SysRoleService sysRoleService;
	
	/**
	 * 角色管理列表
	 * @return
	 */
	@RequestMapping("listUI")
	public String listUI() {
		return "sys/role_list";
	}
	
	/**
	 * 角色列表
	 * @return
	 */
	@RequestMapping("pageUI")
	public String pageUI() {
		return "common/page";
	}
	
	/**
	 * 角色编辑界面
	 */
	@RequestMapping("editUI")
	public String editUI() {
		return "sys/role_edit";
	}
	
	/**
	 * 获取分页数据
	 */
	@RequestMapping("doFindPageObjects")
	@ResponseBody
	public JsonResult doFindPageObjects(Integer pageCurrent, String name) {
		PageInfo<SysRole> pageObject = this.sysRoleService.findPageObjects(pageCurrent, name);
		return new JsonResult(pageObject, "query ok");
	}
	
	/**
	 * 删除数据
	 */
	@RequestMapping("doDeleteObjects")
	@ResponseBody
	public JsonResult doDeleteObjects(Integer[] ids) {
		this.sysRoleService.deleteObjects(ids);
		return new JsonResult("delete ok");
	}
	
	/**
	 * 查询角色
	 */
	@RequestMapping("doFindObjectById")
	@ResponseBody
	public JsonResult doFindObjectById(Integer id) {
		Map<String, Object> map = this.sysRoleService.findObjectById(id);
		return new JsonResult(map, "query ok");
	}
	
	/**
	 * 更新角色
	 */
	@RequestMapping("doUpdateObject")
	@ResponseBody
	public JsonResult doUpdateObject(SysRole role, Integer[] menuIds) {
		this.sysRoleService.updateObject(role, menuIds);
		return new JsonResult("update ok");
	}
	
	/**
	 * 新增角色
	 */
	@RequestMapping("doSaveObject")
	@ResponseBody
	public JsonResult doSaveObject(SysRole role, Integer[] menuIds) {
		this.sysRoleService.insertObject(role, menuIds);
		return new JsonResult("save ok");
	}
	
	@RequestMapping("doFindObjects")
	@ResponseBody
	public JsonResult doFindObjects() {
		List<CheckBox> list = this.sysRoleService.findObjects();
		return new JsonResult(list, "query ok");
	}
}

