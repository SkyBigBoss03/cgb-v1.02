package com.jt.sys.controller;


import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.sys.entity.SysUser;
import com.jt.sys.service.SysLoginService;
import com.jt.sys.service.SysUserService;
import com.jt.sys.vo.JsonResult;

@Controller
@RequestMapping("/")
public class SysLoginController {
	@Autowired
	private SysLoginService sysLoginService;
	
	@Autowired
	private SysUserService sysUserService;
	
	@RequestMapping("loginUI")
	public String loginUI() {
		return "login";
	}
	
	@RequestMapping("doLogin")
	@ResponseBody
	public JsonResult login(String username, String password, String rememberMe, String captext) {
		SysUser user = this.sysUserService.userLogin(username, password, rememberMe, captext);
		return new JsonResult(user, "login ok");
	}
	
	@RequestMapping("doAdminLogin")
	@ResponseBody
	public JsonResult adminLogin(String username, String password, String rememberMe, String captext) {
		SysUser user = this.sysUserService.adminLogin(username, password, rememberMe, captext);
		return new JsonResult(user, "login ok");
	}
	
	@RequestMapping("captcha")
	public void doGetKaptchaImage(HttpServletResponse response) {
		this.sysLoginService.getKaptchaImage(response);
	}
}
