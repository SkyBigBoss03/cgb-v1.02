package com.jt.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


public interface SysUserRoleDao {
	
	/**
	 * 查询用户角色关系数据
	 */
	@Insert("<script>insert into sys_user_roles "
			+ "(user_id, role_id) "
			+ "values "
			+ "<foreach collection=\"roleIds\" item=\"roleId\" separator=\",\"> "
			+ "(#{userId}, #{roleId}) "
			+ "</foreach>"
			+ "</script>")
	int insertObject(
			@Param("userId")Integer userId,
			@Param("roleIds")String[] roleIds);
	
	/**
	 * 根据用户id查询关联的角色id
	 * @param userId
	 * @return
	 */
	@Select("select role_id from sys_user_roles where user_id=#{userId}")
	List<Integer> findRoleIdsByUserId(@Param("userId")Integer userId);
	
	/**
	 * 删除用户角色关系
	 * @param roleIds
	 * @return
	 */
	@Delete("delete from sys_user_roles where user_id=#{userId}")
	int deleteObject(@Param("userId")Integer userId);
	
	/**
	 * 删除多个角色与用户的关系
	 * @param roleIds
	 * @return
	 */
	@Delete("<script>delete from sys_user_roles "
			+ "where role_id in "
			+ "<foreach collection=\"roleIds\" item=\"roleId\" open=\"(\" close=\")\" separator=\",\">#{roleId}"
			+ "</foreach>"
			+ "</script>")
	int deleteObjectsByRoleIds(@Param("roleIds")Integer[] roleIds);
}
