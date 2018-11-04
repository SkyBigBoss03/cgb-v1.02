package com.jt.sys.service.realm;

import org.apache.shiro.authc.UsernamePasswordToken;

public class CustomToken extends UsernamePasswordToken {

	private static final long serialVersionUID = 2041908515549700418L;

	private String loginType;
	private String captext;

	public CustomToken(String username, String password, String rememberMe, String captext, String loginType) {
		super(username, password);
		this.setRememberMe("1".equals(rememberMe));
		this.captext = captext;
		this.loginType = loginType;
	}

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public String getCaptext() {
		return captext;
	}

	public void setCaptext(String captext) {
		this.captext = captext;
	}

}
