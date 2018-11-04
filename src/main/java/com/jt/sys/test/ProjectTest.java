package com.jt.sys.test;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.druid.pool.DruidDataSource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jt.sys.entity.SysRole;
import com.jt.sys.service.SysRoleService;

public class ProjectTest {
	
	private static ClassPathXmlApplicationContext ctx;
	
	@Before
	public void init() {
		ctx = new ClassPathXmlApplicationContext("spring-configs.xml");
	}
	
	@Test
	public void testDataSource() throws SQLException {
		DruidDataSource dataSource = ctx.getBean("dataSource", DruidDataSource.class);
		System.out.println(dataSource.getConnection());
	}
	
	@Test
	public void testSqlSessionFactory() {
		SqlSessionFactory factory = ctx.getBean("sqlSessionFactory", SqlSessionFactory.class);
		SqlSession session = factory.openSession();
		List<Map<String, Object>> list = session.selectList("com.jt.sys.dao.BlogDao.findBlogs");
		for (Map<String, Object> map : list) {
			System.out.println(map);
		}
		session.close();
	}
	
	@Test
	public void testDoFindPageObjects() throws JsonProcessingException {
		SysRoleService sysRoleService = ctx.getBean("sysRoleServiceImpl", SysRoleService.class);
		PageHelper.startPage(1, 2);
		PageInfo<SysRole> pageInfo = sysRoleService.findPageObjects(1, null);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(pageInfo);
		System.out.println(json);
	}
	
	@Test
	public void testSerializable() throws IOException {
		
	}
	
	@Test
	public void testMD5() throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] array = md.digest("123456".getBytes());
		StringBuilder sb = new StringBuilder();
		for(byte b:array){
			String str = Integer.toHexString(b&0xff);
			System.out.println(str);
			if(str.length() == 1){
				str = "0"+str;
			}
			sb.append(str);
		}
		System.out.println(sb.toString());
	}
	
	@After
	public void destroy() {
		ctx.close();
	}
	
}










