package com.jt.sys.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jt.sys.common.exception.ServiceException;
import com.jt.sys.dao.SysMenuDao;
import com.jt.sys.dao.SysRoleMenuDao;
import com.jt.sys.entity.SysMenu;
import com.jt.sys.service.SysMenuService;
import com.jt.sys.util.ShiroUtils;
import com.jt.sys.vo.Node;

@Service
public class SysMenuServiceImpl implements SysMenuService {

	@Autowired
	private SysMenuDao sysMenuDao;

	@Autowired
	private SysRoleMenuDao sysRoleMenuDao;

	@Override
	public List<Map<String, Object>> findObjects() {
		return this.sysMenuDao.findObjects();
	}

	@Override
	public int deleteObject(Integer menuId) {
		if (menuId == null || menuId < 1)
			throw new ServiceException("参数不能为空");
		// 如果有子菜单的话不能直接删除父菜单
		if (this.sysMenuDao.getChildCount(menuId) > 0)
			throw new ServiceException("请先删除子菜单");

		// 如果菜单被角色使用的话，不允许删除
		int counts = this.sysRoleMenuDao.getMenuCount(menuId);
		if (counts > 0)
			throw new ServiceException("该菜单已被角色使用，不能删除");

		int rows = 0;
		try {
			rows = this.sysMenuDao.deleteObject(menuId);
			if (rows == 0)
				throw new ServiceException("此菜单可能已不存在");

			this.sysRoleMenuDao.deleteObjectsByMenuId(menuId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("服务器正在维护");
		}
		return rows;
	}

	@Override
	public List<Node> findZtreeMenuNodes() {
		return this.sysMenuDao.findZtreeMenuNodes();
	}

	@Override
	public int insertObject(SysMenu sysMenu) {
		if (sysMenu == null)
			throw new ServiceException("参数不能为空");
		if (StringUtils.isEmpty(sysMenu.getName()))
			throw new ServiceException("菜单名称不能为空");

		// 设置创建用户
		String modifiedUser = ShiroUtils.getPrincipal().getUsername();
		sysMenu.setModifiedUser(modifiedUser);
		sysMenu.setCreatedUser(modifiedUser);

		int rows = 0;
		try {
			rows = this.sysMenuDao.insertObject(sysMenu);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("服务器正在维护中");
		}
		return rows;
	}

	@Override
	public Map<String, Object> findObjectByMenuId(Integer menuId) {
		if (menuId == null || menuId < 1)
			throw new ServiceException("参数不能为空");
		Map<String, Object> map = this.sysMenuDao.findObjectByMenuId(menuId);
		if (map == null)
			throw new ServiceException("此记录已不存在");
		return map;
	}

	@Override
	public int updateObject(SysMenu sysMenu) {
		if (sysMenu == null || sysMenu.getId() == null || sysMenu.getId() < 1)
			throw new ServiceException("参数不能为空");
		if (StringUtils.isEmpty(sysMenu.getName()))
			throw new ServiceException("菜单名称不能为空");

		// 设置创建用户
		String modifiedUser = ShiroUtils.getPrincipal().getUsername();
		sysMenu.setModifiedUser(modifiedUser);

		int rows = 0;
		try {
			rows = this.sysMenuDao.updateObject(sysMenu);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("服务器正在维护中");
		}
		if (rows == 0)
			throw new ServiceException("修改的数据可能已不存在");
		return rows;
	}
}
