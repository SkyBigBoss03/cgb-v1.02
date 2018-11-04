package com.jt.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.sys.entity.SysMenu;
import com.jt.sys.service.SysMenuService;
import com.jt.sys.vo.JsonResult;

@Controller
@RequestMapping("/menu/")
public class SysMenuController {

	@Autowired
	private SysMenuService sysMenuService;
	
	@RequestMapping("listUI")
	public String listUI() {
		return "sys/menu_list";
	}
	
	@RequestMapping("editUI")
	public String editUI() {
		return "sys/menu_edit";
	}
	
	@RequestMapping("doFindObjects")
	@ResponseBody
	public JsonResult doFindObjects() {
		return new JsonResult(this.sysMenuService.findObjects(), "query ok");
	} 
	
	@RequestMapping("doDeleteObject")
	@ResponseBody
	public JsonResult doDeleteObject(Integer menuId) {
		this.sysMenuService.deleteObject(menuId);
		return new JsonResult("delete ok");
	}
	
	@RequestMapping("doFindZtreeMenuNodes")
	@ResponseBody
	public JsonResult doFindZtreeMenuNodes() {
		return new JsonResult(this.sysMenuService.findZtreeMenuNodes(), "query ok");
	}
	
	@RequestMapping("doInsertObject")
	@ResponseBody
	public JsonResult doInsertObject(SysMenu sysMenu) {
		this.sysMenuService.insertObject(sysMenu);
		return new JsonResult("save ok");
	}
	
	@RequestMapping("doFindObjectById")
	@ResponseBody
	public JsonResult doFindObjectById(Integer menuId){
		return new JsonResult(this.sysMenuService.findObjectByMenuId(menuId), "query ok");
	}
	
	@RequestMapping("doUpdateObject")
	@ResponseBody
	public JsonResult doUpdateObject(SysMenu sysMenu) {
		this.sysMenuService.updateObject(sysMenu);
		return new JsonResult("update ok");
	}
}
