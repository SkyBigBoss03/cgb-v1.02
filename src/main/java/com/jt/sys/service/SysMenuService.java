package com.jt.sys.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.jt.sys.entity.SysMenu;
import com.jt.sys.vo.Node;

public interface SysMenuService {

	List<Map<String, Object>> findObjects();
	
	int deleteObject(Integer menuId);
	
	List<Node> findZtreeMenuNodes();
	
	int insertObject(SysMenu sysMenu);
	
	Map<String, Object> findObjectByMenuId(@Param("id")Integer menuId);
	
	int updateObject(SysMenu sysMenu);
}
