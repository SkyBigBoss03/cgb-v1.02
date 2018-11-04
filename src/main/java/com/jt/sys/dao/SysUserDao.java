package com.jt.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;

import com.jt.sys.entity.SysUser;

public interface SysUserDao extends SysCommonDao {	
	
	public class UserDynamicSqlProvider {
		public String findObjects(final String username) {
			return new SQL(){{
				SELECT("id, username, salt, email, mobile, valid, createdTime, modifiedTime, createdUser, modifiedUser");
				FROM("sys_users");
				if (username != null && !"".equals(username)) {
					WHERE("username like concat('%', #{username}, '%')");
				}
				ORDER_BY("createdTime desc");
			}}.toString();
		}
		
		public String getRowCounts(final String username) {
			return new SQL(){{
				SELECT("count(*)");
				FROM("sys_users");
				if (username != null && !"".equals(username)) {
					WHERE("username like concat('%', #{username}, '%')");
				}
			}}.toString();
		}
		
		public String validById(final Integer id, final Integer valid, final String modifiedUser) {
			return new SQL(){{
				UPDATE("sys_users");
				if (modifiedUser != null && !"".equals(modifiedUser)) {
					SET("modifiedUser = #{modifiedUser}");
				}
				SET("valid = #{valid}");
				SET("modifiedTime = now()");
				WHERE("id=#{id}");
			}}.toString();
		}

		public String updateObject(final SysUser sysUser) {
			String sqlString = new SQL(){{
				UPDATE("sys_users");
				if (sysUser.getUsername() != null && !"".equals(sysUser.getUsername())) {
					SET("username = #{username}");
				}
				if (sysUser.getPassword() != null && !"".equals(sysUser.getPassword())) {
					SET("password = #{password}");
					SET("salt = #{salt}");
				} 
				if (sysUser.getEmail() != null && !"".equals(sysUser.getEmail())) {
					SET("email = #{email}");
				}
				if (sysUser.getMobile() != null && !"".equals(sysUser.getMobile())) {
					SET("mobile = #{mobile}");
				}
				if (sysUser.getModifiedUser() != null && !"".equals(sysUser.getModifiedUser())) {
					SET("modifiedUser = #{modifiedUser}");
				}
				SET("valid = 1");
				SET("modifiedTime = now()");
				WHERE("id = #{id}");
			}}.toString();
			System.out.println("update:"+sqlString);
			return sqlString;
		}
	}
	
	/**
	 * 查询所有用户
	 * @return
	 */
	@SelectProvider(type = UserDynamicSqlProvider.class, method = "findObjects")
	List<SysUser> findObjects(@Param("username")String username);
	
	/**
	 * 查询所有用户
	 * @return
	 */
	@Select("select * from sys_users")
	List<SysUser> findAllObjects();
	
	/**
	 * 获取用户总记录数,这里使用@Param注解是因为动态sql的需要
	 * @param name
	 * @return
	 */
	@SelectProvider(type = UserDynamicSqlProvider.class, method = "getRowCounts")
	int getRowCounts(@Param("username")String username);
	
	// 对用户信息执行禁用或启用操作
	@UpdateProvider(type = UserDynamicSqlProvider.class, method = "validById")
	int validById(
			@Param("id")Integer id, 
			@Param("valid")Integer valid, 
			@Param("modifiedUser")String modifiedUser);
	
	/**
	 * 插入用户数据
	 * @param sysUser
	 * @return
	 */
	@Insert("insert into sys_users "
			+ "(username,password,email,mobile,salt,valid,createdTime,modifiedTime,createdUser,modifiedUser) "
			+ "values "
			+ "(#{username}, #{password}, #{email}, #{mobile}, #{salt}, #{valid}, now(), now(), #{createdUser}, #{modifiedUser})")
	@Options(useGeneratedKeys=true, keyProperty="id")
	int insertObject(SysUser sysUser);
	
	/**
	 * 根据id查询用户信息
	 * @param id
	 * @return
	 */
	@Select("select id, username, password, email, mobile from sys_users where id=#{id}")
	SysUser findObjectById(@Param("id")Integer id);
	
	/**
	 * 修改用户数据
	 * @param sysUser
	 * @return
	 */
	@UpdateProvider(type = UserDynamicSqlProvider.class, method = "updateObject")
	int updateObject(SysUser sysUser);
	
	/**
	 * 根据用户名查询用户，用户名作为约束存在不能有重复
	 * @param username
	 * @return
	 */
	@Select("select u.*, r.name roleName "
			+ "from sys_users u "
			+ "join sys_user_roles ur "
			+ "join sys_roles r "
			+ "on u.id=ur.user_id "
			+ "and ur.role_id=r.id "
			+ "where u.username=#{username}")
	SysUser findObjectByUsername(@Param("username")String username);
	
	/**
	 * 根据用户名查询管理员
	 * @param username
	 * @return
	 */
	@Select("select u.*, r.name roleName "
			+ "from sys_roles r "
			+ "join sys_user_roles ur "
			+ "join sys_users u "
			+ "on r.id=ur.role_id "
			+ "and ur.user_id=u.id "
			+ "where u.username=#{username} and r.id=1")
	SysUser findAdminByUsername(@Param("username")String username);
	
	/**
	 * 根据用户id查询该用户的权限
	 * @param id
	 * @return
	 */
	@Select("select m.permission "
			+ "from sys_menus m "
			+ "join sys_role_menus rm "
			+ "join sys_user_roles ur "
			+ "join sys_users u "
			+ "ON "
			+ "m.id=rm.menu_id "
			+ "and "
			+ "rm.role_id=ur.role_id "
			+ "and "
			+ "ur.user_id=u.id "
			+ "where u.id=#{id}")
	List<String> findUserPermissions(@Param("id")Integer id);
}
