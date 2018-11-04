package com.jt.sys.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.jt.sys.entity.SysMenu;
import com.jt.sys.vo.Node;

public interface SysMenuDao {

	/**
	 * 查询所有菜单一级上一级菜单信息
	 * @return
	 */
	@Select("select c.*, (select p.name from sys_menus p where p.id = c.parentId) parentName from sys_menus c")
	List<Map<String, Object>> findObjects();
	
	/**
	 * 根据菜单id获取详细数据，包含parentName
	 * @param menuId
	 * @return
	 */
	@Select("select c.*, (select p.name from sys_menus p where c.parentId=p.id) parentName from sys_menus c where c.id=#{id}")
	Map<String, Object> findObjectByMenuId(@Param("id")Integer menuId);
	
	/**
	 * 查询菜单id是否有子菜单
	 */
	@Select("select count(*) from sys_menus where parentId=#{id}")
	int getChildCount(@Param("id")Integer id);
	
	/**
	 * 根据菜单id删除菜单
	 * @param id
	 * @return
	 */
	@Delete("delete from sys_menus where id=#{id}")
	int deleteObject(@Param("id")Integer id);
	
	/**
	 * 获取数据库中所有的菜单信息
	 * @return
	 */
	@Select("select id, name, parentId from sys_menus")
	List<Node> findZtreeMenuNodes();
	
	/**
	 * 新增菜单元素
	 * @param sysMenu
	 * @return
	 */
	@Insert("insert into sys_menus "
			+ "(name, url, type, sort, note, parentId, permission, createdTime, modifiedTime, createdUser, modifiedUser) "
			+ "values "
			+ "(#{name}, #{url}, #{type}, #{sort}, #{note}, #{parentId}, #{permission}, now(), now(), #{createdUser}, #{modifiedUser})")
	int insertObject(SysMenu sysMenu);
	
	/**
	 * 修改菜单元素
	 * @param sysMenu
	 * @return
	 */
	@Update("update sys_menus set "
			+ "name=#{name}, "
			+ "type=#{type}, "
			+ "sort=#{sort}, "
			+ "url=#{url}, "
			+ "parentId=#{parentId}, "
			+ "permission=#{permission}, "
			+ "modifiedUser=#{modifiedUser}, "
			+ "modifiedTime=now() "
			+ "where id=#{id}")
	int updateObject(SysMenu sysMenu);
}
