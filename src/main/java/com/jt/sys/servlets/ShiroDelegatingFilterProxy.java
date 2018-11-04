package com.jt.sys.servlets;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import org.springframework.web.filter.DelegatingFilterProxy;

@WebFilter(filterName="DelegatingFilterProxy", urlPatterns="/*", initParams={
		@WebInitParam(name="targetBeanName", value="shiroFilter")	
})
public class ShiroDelegatingFilterProxy extends DelegatingFilterProxy {

}
