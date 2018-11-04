package com.jt.sys.initializer;

import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.jt.sys.config.RootConfig;
import com.jt.sys.config.ServletConfig;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[]{RootConfig.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[]{ServletConfig.class};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"*.do"};
	}
	
	@Override
	protected void customizeRegistration(Dynamic registration) {
		registration.setLoadOnStartup(1);
		registration.setAsyncSupported(true);
	}
}
