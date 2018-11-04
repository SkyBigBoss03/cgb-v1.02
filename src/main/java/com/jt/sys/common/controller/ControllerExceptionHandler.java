package com.jt.sys.common.controller;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.sys.common.exception.CustomAuthUserException;
import com.jt.sys.common.exception.ServiceException;
import com.jt.sys.vo.JsonResult;

@ControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(ServiceException.class)
	@ResponseBody
	public JsonResult handlerException(ServiceException e) {
		e.printStackTrace();
		return new JsonResult(e);
	}
	
	@ExceptionHandler(CustomAuthUserException.class)
	@ResponseBody
	public JsonResult handlerAuthUserException(CustomAuthUserException e) {
		e.printStackTrace();
		return new JsonResult(e);
	}
	
	@ExceptionHandler(AuthenticationException.class)
	@ResponseBody
	public JsonResult handlerAuthenticationException(AuthenticationException e) {
		e.printStackTrace();
		return new JsonResult(new ServiceException("用户名或密码错误"));
	}
	
	@ExceptionHandler(AuthorizationException.class)
	@ResponseBody
	public JsonResult handlerAuthorizingException(AuthorizationException e) {
		e.printStackTrace();
		return new JsonResult(new ServiceException("用户无权限进行该操作"));
	}
	
}
