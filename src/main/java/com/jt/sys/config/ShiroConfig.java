package com.jt.sys.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.SessionValidationScheduler;
import org.apache.shiro.session.mgt.ValidatingSessionManager;
import org.apache.shiro.session.mgt.quartz.QuartzSessionValidationScheduler;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.jt.sys.service.realm.CustomModularRealmAuthenticator;
import com.jt.sys.service.realm.ShiroAdminRealm;
import com.jt.sys.service.realm.ShiroUserRealm;
import com.jt.sys.service.realm.SystemLogoutFilter;

@Configuration
public class ShiroConfig {

	/**
	 * 配置算法匹配器
	 * @return
	 */
	@Bean
	public HashedCredentialsMatcher getHashedCredentialsMatcher(){
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
		// 使用MD5算法
		matcher.setHashAlgorithmName("MD5");
		// 使用1024次加密算法
//		matcher.setHashIterations(1024);
		return matcher;
	}
	
	/**
	 * 配置自定义用户认证realm
	 * @param matcher
	 * @return
	 */
	@Bean
	public ShiroUserRealm getShiroUserRealm(@Autowired HashedCredentialsMatcher matcher) {		
		ShiroUserRealm realm = new ShiroUserRealm();
		realm.setCredentialsMatcher(matcher);
		return realm;
	}
	
	/**
	 * 配置自定义管理员认证realm
	 * @param matcher
	 * @return
	 */
	@Bean
	public ShiroAdminRealm getShiroAdminRealm(@Autowired HashedCredentialsMatcher matcher) {
		ShiroAdminRealm realm = new ShiroAdminRealm();
		realm.setCredentialsMatcher(matcher);
		return realm;
	}
	
	/**
	 * 配置shiro缓存管理器
	 */
	@Bean
	public EhCacheManager getCacheManager(){
		EhCacheManager cacheManager = new EhCacheManager();
		cacheManager.setCacheManagerConfigFile("classpath:ehcache.xml");
		return cacheManager;
	}
	
	/**
	 * 配置shiro记住我管理器
	 */
	@Bean
	public RememberMeManager getRememberMeManager() {
		CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
		rememberMeManager.setCookie(getRememberMeCookie());
		return rememberMeManager;
	}
	
	/**
	 * 配置记住我的cookie
	 * @return
	 */
	@Bean("rememberMeCookie")
	public Cookie getRememberMeCookie(){
		SimpleCookie sc = new SimpleCookie("rememberMe");
		// 保证该系统不会受到跨域的脚本操作供给
		sc.setHttpOnly(true);
		// cookie有效时间的设置，一个月的有效期，如果设置为-1表示浏览器关闭，则Cookie消失
		sc.setMaxAge(2592000);
		return sc;
	}
	
	/**
	 * 配置自定义的realm认证器
	 * @return
	 */
	@Bean
	public CustomModularRealmAuthenticator getCustomModularRealmAuthenticator() {
		CustomModularRealmAuthenticator cAuthenticator = new CustomModularRealmAuthenticator();
		// 配置认证策略，至少有一个成功才算认证成功
		AtLeastOneSuccessfulStrategy atLeast = new AtLeastOneSuccessfulStrategy();
		cAuthenticator.setAuthenticationStrategy(atLeast);
		return cAuthenticator;
	}
	
	/**
	 * 配置SessionManager会话管理器
	 */	
	@Bean
	public SessionManager getSessionManager() {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		// 定义全局的会话超时时间，单位是毫秒，这里设置3个小时
		sessionManager.setGlobalSessionTimeout(10800000);
		// 设置删除所有无效的session，此时的session被保存在了内存当中
		sessionManager.setDeleteInvalidSessions(true);
		// 设置要使用的无效的session的定时调度器
		sessionManager.setSessionValidationScheduler(null);
		// 需要让此session可以使用该定时调度器，如果用户没有进行退出操作的话，需要通过定时调度任务去进行清理过期的session
		sessionManager.setSessionValidationSchedulerEnabled(true);
		// 定义session可以使用的序列化工具类(如果securityManager配置了CacheManager的话，会自动创建EnterpriseCacheSessionDAO的实例)
//		sessionManager.setSessionDAO(sessionDao);
		// 所有的session一定要将id设置到cookie当中，需要提供cookie的操作模板
		sessionManager.setSessionIdCookie(getSessionIdCookie());
		// 设置开启使用cookie操作模板
		sessionManager.setSessionIdCookieEnabled(true);
		return sessionManager;
	}
	
	/**
	 * 配置session调度器
	 */
	@Bean
	public SessionValidationScheduler getSessionValidationScheduler(@Autowired ValidatingSessionManager sessionManager) {
		QuartzSessionValidationScheduler sessionValidationScheduler = new QuartzSessionValidationScheduler();
		// 设置session的失效扫描间隔，单位为毫秒，1个小时执行一次
		sessionValidationScheduler.setSessionValidationInterval(360000);
		sessionValidationScheduler.setSessionManager(sessionManager);
		return sessionValidationScheduler;
	}
	
	/**
	 * 配置需要向Cookie中保存数据的配置模版
	 * 定义session与客户端的之间的联系，为了进行有效的session管理所以还需要建立有一个Cookie的操作模版
	 * DefaultWebSessionManager无参构造方法默认创建了一个SimpleCookie，时间默认是-1
	 * @return
	 */
	@Bean("sessionIdCookie")
	public Cookie getSessionIdCookie(){
		SimpleCookie sc = new SimpleCookie(ShiroHttpSession.DEFAULT_SESSION_ID_NAME);
		// 保证该系统不会受到跨域的脚本操作供给
		sc.setHttpOnly(true);
		// cookie有效时间的设置，30分钟的有效期，如果设置为-1表示浏览器关闭，则Cookie消失
		sc.setMaxAge(180000);
		return sc;
	}
	
	/**
	 * 配置shiro安全管理器
	 */
	@Bean
	public SecurityManager getSecurityManager(
			@Autowired ShiroUserRealm userRealm,
			@Autowired ShiroAdminRealm adminRealm,
			@Autowired EhCacheManager cacheManager,
			@Autowired RememberMeManager rememberMeManager,
			@Autowired CustomModularRealmAuthenticator cAuthenticator,
			@Autowired SessionManager sessionManager){
		DefaultWebSecurityManager sm = new DefaultWebSecurityManager();
		Collection<Realm> realms = new ArrayList<>();
		realms.add(userRealm);
		realms.add(adminRealm);
		sm.setAuthenticator(cAuthenticator);
		sm.setRealms(realms);
		sm.setRememberMeManager(rememberMeManager);
		sm.setSessionManager(sessionManager);
		sm.setCacheManager(cacheManager);
		return sm;
	}
	
	/**
	 * 配置shiro过滤器
	 * @throws Exception 
	 */
	@Bean("shiroFilter")
	public Filter getShiroFilterFactoryBean(
			@Autowired SecurityManager securityManager,
			@Autowired SystemLogoutFilter systemLogoutFilter) throws Exception{
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		shiroFilterFactoryBean.setLoginUrl("/loginUI.do");
		// 配置退出过滤器
		Map<String, Filter> map = new HashMap<>();
		map.put("logout", systemLogoutFilter);
		shiroFilterFactoryBean.setFilters(map);
		// shiro连接约束配置
		shiroFilterFactoryBean.setFilterChainDefinitions(""
				+ "/bower_components/** = anon\n"
				+ "/build/** = anon\n"
				+ "/dist/** = anon\n"
				+ "/plugins/** = anon\n"
				+ "/doLogin.do = anon\n"
				+ "/doAdminLogin.do = anon\n"
				+ "/captcha.do = anon\n"
				+ "/druid = anon\n"
				+ "/doLogout.do = logout\n"	// 配置退出
				+ "/** = user"		// 配置记住我或认证通过可以访问的地址
				);
		return (Filter)shiroFilterFactoryBean.getObject();
	}
	
	/**
	 * 配置shiro生命周期处理器
	 */
	@Bean("lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor(){
		LifecycleBeanPostProcessor processor = new LifecycleBeanPostProcessor();
		return processor;
	}
	
	@Bean
	@DependsOn("lifecycleBeanPostProcessor")
	public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator(){
		DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
		return creator;
	}
	
	/**
	 * 配置shiro权限注释检查
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(@Autowired SecurityManager sm){
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(sm);
		return advisor;
	}
}
