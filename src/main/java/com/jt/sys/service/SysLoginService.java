package com.jt.sys.service;

import javax.servlet.http.HttpServletResponse;

public interface SysLoginService {

	/**
	 * 获取图片验证码
	 * @param response
	 */
	void getKaptchaImage(HttpServletResponse response);
	
}
