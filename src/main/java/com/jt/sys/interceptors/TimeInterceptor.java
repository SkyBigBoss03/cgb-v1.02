package com.jt.sys.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * spring的拦截器，实现接口HandlerInterceptor，延迟加载lazy-init对拦截器无效，它在服务器启动的时候立即创建
 * @author Administrator
 *
 */
public class TimeInterceptor implements HandlerInterceptor { 

	public TimeInterceptor() {
		System.out.println("TimeInterceptor.TimeInterceptor()");
	}
	
	/**
	 * controller的方法执行前执行
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("TimeInterceptor.preHandle()");
		long startTime = System.nanoTime();
		request.setAttribute("startTime", startTime);
		// true表示放行， fasle表示拦截
		return true;
	}

	/**
	 * controller的方法执行后执行
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	/**
	 * 整个web页面渲染过程完成完后执行
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		System.out.println("TimeInterceptor.afterCompletion()");
		long endTime = System.nanoTime();
		long startTime = (Long)request.getAttribute("startTime");
		System.out.println("方法执行时间："+(endTime-startTime));
	}

}
