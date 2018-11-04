package com.jt.sys.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.jt.sys.entity.SysRole;
import com.jt.sys.vo.CheckBox;

public interface SysRoleService {

	/**
	 * 本方法中要分页查询角色信息,并查询角色总记录数据
	 * @param pageCurrent 当表要查询的当前页的页码值
	 * @return 封装当前实体数据以及分页信息
	 */
	PageInfo<SysRole> findPageObjects(Integer pageCurrent, String name);
	
	/**
	 * 根据id删除数据
	 * @param ids
	 * @return
	 */
	int deleteObjects(Integer[] ids);
	
	/**
	 * 根据id获取角色信息
	 * @param id
	 * @return
	 */
	Map<String, Object> findObjectById(Integer id);
	
	/**
	 * 更新角色数据
	 * @param role
	 * @return
	 */
	int updateObject(SysRole role, Integer[] menuIds);
	
	/**
	 * 插入新的角色数据
	 * @param role
	 * @return
	 */
	int insertObject(SysRole role, Integer[] menuIds);
	
	/**
	 * 获取所有的角色信息
	 * @return
	 */
	List<CheckBox> findObjects();
}
