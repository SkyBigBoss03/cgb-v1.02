package com.jt.sys.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * spring拦截器 继承HandlerInterceptorAdapter写法
 * @author Administrator
 *
 */
public class LogInterceptor extends HandlerInterceptorAdapter {

	public LogInterceptor() {
		System.out.println("LogInterceptor.LogInterceptor()");
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("LogInterceptor.preHandle()");
		return true;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		System.out.println("LogInterceptor.afterCompletion()");
	}
	
}
