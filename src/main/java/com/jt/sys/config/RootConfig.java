package com.jt.sys.config;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageInterceptor;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;


@PropertySource(name = "cfg", value = "classpath:config.properties")
@org.springframework.context.annotation.Configuration
@ComponentScan("com.jt.sys.service")
// 开启声明式事务管理注解方式，默认使用代理
@EnableTransactionManagement()
@Import(ShiroConfig.class)
public class RootConfig {

	static {
		org.apache.ibatis.logging.LogFactory.useLog4JLogging();
	}
	
	@Bean(name="dataSource", initMethod="init", destroyMethod="close")
	public DruidDataSource getDruidDataSource(@Value("${driver}") String driverClass, @Value("${url}") String url,
			@Value("${user}") String user, @Value("${pwd}") String pwd) throws SQLException {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(url);
		dataSource.setUsername(user);
		dataSource.setPassword(pwd);
		// 设置sql日志监控
		dataSource.setFilters("stat,log4j");
		return dataSource;
	}

	@Bean
	public PageInterceptor getPageInterceptor() {
		PageInterceptor pageInterceptor = new PageInterceptor();
		Properties props = new Properties();
		props.setProperty("helperDialect", "mysql");
		props.setProperty("reasonable", "true");
		pageInterceptor.setProperties(props);
		return pageInterceptor;
	}
	
	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactoryBean getSqlSessionFactoryBean(
			@Autowired DataSource dataSource,
			@Autowired PageInterceptor pageInterceptor) {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		// 设置数据源
		factoryBean.setDataSource(dataSource);
		// 设置分页插件
		Interceptor[] plugins = new Interceptor[]{(Interceptor)pageInterceptor};
		factoryBean.setPlugins(plugins);
		// 配置
		Configuration cfg = new Configuration();
		cfg.addMappers("com.jt.sys.dao");
		factoryBean.setConfiguration(cfg);
		return factoryBean;
	}
	
	/**
	 * 配置mybatis的Dao动态代理
	 * @return
	 */
	@Bean
	public MapperScannerConfigurer getMapperScannerConfigurer() {
		MapperScannerConfigurer msc = new MapperScannerConfigurer();
		msc.setBasePackage("com.jt.sys.dao");
		msc.setSqlSessionFactoryBeanName("sqlSessionFactory");
		return msc;
	}

	/**
	 * 配置验证码
	 */
	@Bean
	public Producer getProducer() {
		DefaultKaptcha producer = new DefaultKaptcha();
		Properties props = new Properties();
		props.setProperty("kaptcha.border", "yes");
		props.setProperty("kaptcha.border.color", "105,179,90");
		props.setProperty("kaptcha.textproducer.font.color", "blue");
		props.setProperty("kaptcha.image.width", "125");
		props.setProperty("kaptcha.image.height", "45");
		props.setProperty("kaptcha.textproducer.font.size", "45");
		props.setProperty("kaptcha.session.key", "code");
		props.setProperty("kaptcha.textproducer.char.length", "4");
		props.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
		
		Config config = new Config(props);
		producer.setConfig(config);
		return producer;
	}
	
	/**
	 * 配置事务管理类
	 */
	@Bean
	public DataSourceTransactionManager getDataSourceTransactionManager(@Autowired DataSource dataSource) {
		DataSourceTransactionManager manager = new DataSourceTransactionManager();
		manager.setDataSource(dataSource);
		return manager;
	}
	
	
}

















