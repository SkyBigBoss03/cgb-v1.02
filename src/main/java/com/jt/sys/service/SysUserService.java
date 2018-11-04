package com.jt.sys.service;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.github.pagehelper.PageInfo;
import com.jt.sys.entity.SysUser;

public interface SysUserService {

	/**
	 * 登录认证操作
	 */
	SysUser userLogin(String username, String password, String rememberMe, String captext);
	
	/**
	 * 管理员登陆操作
	 */
	SysUser adminLogin(String username, String password, String rememberMe, String captext);
	
	PageInfo<SysUser> findPageObjects(Integer currentPage, String username);
	
	int validById(Integer id, Integer valid, String modifiedUser);
	
	int saveObject(SysUser sysUser, String[] roleIds);
	
	Map<String, Object> findObjectById(Integer id);
	
	int updateObject(SysUser sysUser, String[] roleIds);
	
	void exportExcelUsers(HttpServletResponse response);
	
	void exportPDFUsers(HttpServletResponse response);
}
