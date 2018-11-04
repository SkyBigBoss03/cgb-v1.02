package com.jt.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 角色菜单关系表映射
 * @author Administrator
 *
 */
public interface SysRoleMenuDao {
	/**
	 * 根据菜单id删除角色菜单关系表的数据
	 * @param menuId
	 * @return
	 */
	@Delete("delete from sys_role_menus where menu_id=#{menuId}")
	int deleteObjectsByMenuId(@Param("menuId")Integer menuId);
	
	/**
	 * 根据角色id获取关联的菜单id
	 * @param roleId
	 * @return
	 */
	@Select("select menu_id from sys_role_menus where role_id=#{roleId}")
	List<Integer> findMenuIdsByRoleId(@Param("roleId")Integer roleId);
	
	/**
	 * 插入角色菜单关系数据
	 * @param roleId
	 * @param menuIds
	 * @return
	 */
	@Insert("<script>insert into "
			+ "sys_role_menus (role_id, menu_id) "
			+ "values "
			+ "<foreach collection=\"menuIds\" item=\"menuId\" separator=\",\"> (#{roleId}, #{menuId})</foreach>"
			+ "</script>")
	int insertObject(
			@Param("roleId")Integer roleId, 
			@Param("menuIds")Integer[] menuIds);
	
	/**
	 * 删除该角色与菜单的关系数据
	 * @param roleId
	 * @return
	 */
	@Delete("delete from sys_role_menus where role_id=#{roleId}")
	int deleteObjects(@Param("roleId")Integer roleId);
	
	/**
	 * 删除多个角色与菜单的关系数据
	 * @param roleIds
	 * @return
	 */
	@Delete("<script>delete from sys_role_menus "
			+ "where role_id in "
			+ "<foreach collection=\"roleIds\" item=\"roleId\" open=\"(\" close=\")\" separator=\",\">"
			+ "#{roleId}"
			+ "</foreach>"
			+ "</script>")
	int deleteObjectsByRoleIds(@Param("roleIds")Integer[] roleIds);
	
	/**
	 * 获取菜单与角色的数据
	 * @param menuId
	 * @return
	 */
	@Select("select count(*) from sys_role_menus where menu_id=#{menuId}")
	int getMenuCount(@Param("menuId")Integer menuId);
}
