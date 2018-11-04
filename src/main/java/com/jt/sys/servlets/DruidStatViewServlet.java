package com.jt.sys.servlets;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.alibaba.druid.support.http.StatViewServlet;

@WebServlet(urlPatterns="/druid/*",
			initParams={
					// IP白名单
					@WebInitParam(name="allow",value="176.217.5.116,127.0.0.1"),
					// 用户名
					@WebInitParam(name="loginUsername", value="root"),
					// 密码
					@WebInitParam(name="loginPassword", value="root"),
					// 禁用html页面上的resetAll功能
					@WebInitParam(name="resetEnable", value="true")
			})
public class DruidStatViewServlet extends StatViewServlet {

	private static final long serialVersionUID = 2903642449378380809L;

}
