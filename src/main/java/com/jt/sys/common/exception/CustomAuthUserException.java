package com.jt.sys.common.exception;

import org.apache.shiro.authc.AuthenticationException;

public class CustomAuthUserException extends AuthenticationException {
	private static final long serialVersionUID = 5827318923096014795L;
	public CustomAuthUserException(String msg) {
		super(msg);
	}
}
