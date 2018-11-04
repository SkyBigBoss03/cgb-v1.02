package com.jt.sys.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.util.StringUtils;

import com.jt.sys.entity.SysUser;

public class ShiroUtils {

	private ShiroUtils(){}
	
	/**
	 * 获取主体用户
	 */
	public static SysUser getPrincipal(){
		Subject subject = SecurityUtils.getSubject();
		SysUser sysUser = (SysUser)subject.getPrincipals().getPrimaryPrincipal();
		if(StringUtils.isEmpty(sysUser)) {
			SysUser tmpSysUser = new SysUser();
			tmpSysUser.setUsername(null);
			return tmpSysUser;
		}
		return sysUser;
	}
	
	public static Session getSession() {
		return SecurityUtils.getSubject().getSession();
	}
	
	public static void setSessionAttribute(String key, Object value) {
		getSession().setAttribute(key, value);
	}
	
	public static Object getSessionValue(String key) {
		return getSession().getAttribute(key);
	}
	
}
