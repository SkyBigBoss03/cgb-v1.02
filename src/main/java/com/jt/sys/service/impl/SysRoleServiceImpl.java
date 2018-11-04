package com.jt.sys.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jt.sys.common.exception.ServiceException;
import com.jt.sys.dao.SysRoleDao;
import com.jt.sys.dao.SysRoleMenuDao;
import com.jt.sys.dao.SysUserRoleDao;
import com.jt.sys.entity.SysRole;
import com.jt.sys.service.SysRoleService;
import com.jt.sys.util.ShiroUtils;
import com.jt.sys.vo.CheckBox;

@Service
public class SysRoleServiceImpl implements SysRoleService {

	@Autowired
	private SysRoleDao sysRoleDao;
	@Autowired
	private SysRoleMenuDao sysRoleMenuDao;
	@Autowired
	private SysUserRoleDao sysUserRoleDao;

	@Override
	public PageInfo<SysRole> findPageObjects(Integer pageCurrent, String name) {
		if (pageCurrent == null || pageCurrent < 1) {
			throw new ServiceException("页码数据不正确");
		}
		
		/*
		// 定义分页大小，计算当前页的开始索引
		Integer pageSize = 2;
		Integer startIndex = (pageCurrent - 1) * pageSize;
		// 从数据库获取角色记录的数据
		List<SysRole> sysRoles = this.sysRoleDao.findPageObjects(startIndex, pageSize, name);
		// 获取角色的总记录数
		int rowCount = this.sysRoleDao.getRowCount(name);
		// 封装返回的数据到pageObject
		PageObject<SysRole> pageObject = new PageObject<>();
		pageObject.setPageCurrent(pageCurrent);
		pageObject.setPageSize(pageSize);
		pageObject.setRecords(sysRoles);
		pageObject.setRowCount(rowCount);
		*/
		
		// 使用pageHelper进行分页控制
		PageHelper.startPage(pageCurrent, 2);
		List<SysRole> roles = this.sysRoleDao.findObjects(name);
		PageInfo<SysRole> pageInfo = new PageInfo<>(roles);
		return pageInfo;
	}

	@Override
	public int deleteObjects(Integer[] ids) {
		if (ids.length == 0) {
			throw new ServiceException("请选择有效id进行删除");
		}
		int rows = 0;
		try {
			// 删除角色数据
			rows = this.sysRoleDao.deleteObject(ids);
			// 删除角色与菜单关联的数据
			this.sysRoleMenuDao.deleteObjectsByRoleIds(ids);
			// 删除角色与用户关联的数据
			this.sysUserRoleDao.deleteObjectsByRoleIds(ids);
		} catch (Throwable e) {
			// 系统报警：给维护人员发短信
			e.printStackTrace();
			throw new ServiceException("服务器维护中");
		}
		if (rows <= 0) {
			throw new ServiceException("数据可能已不存在");
		}
		return rows;
	}

	@Override
	public Map<String, Object> findObjectById(Integer id) {
		if (id == null || id < 1) {
			throw new ServiceException("无效的id");
		}
		SysRole role = this.sysRoleDao.findObjectById(id);
		List<Integer> menuIds = this.sysRoleMenuDao.findMenuIdsByRoleId(id);
		Map<String, Object> map = new HashMap<>();
		map.put("role", role);
		map.put("menuIds", menuIds);
		if (role == null || role.getId() == null) {
			throw new ServiceException("查询的id不存在");
		}
		return map;
	}

	@Override
	public int updateObject(SysRole role, Integer[] menuIds) {
		if (role == null)
			throw new ServiceException("更新的对象不能为空");
		if (role.getId() == null)
			throw new ServiceException("id的值不能为空");
		if (StringUtils.isEmpty(role.getName()))
			throw new ServiceException("角色名不能为空");
		if (StringUtils.isEmpty(menuIds))
			throw new ServiceException("请为该角色选择权限");

		SysRole tmpRole = this.sysRoleDao.findObjectById(role.getId());
		if (tmpRole == null)
			throw new ServiceException("id可能已不存在");

		// 设置创建用户
		String modifiedUser = ShiroUtils.getPrincipal().getUsername();
		role.setModifiedUser(modifiedUser);

		int rows = 0;
		try {
			// 新增角色数据
			rows = this.sysRoleDao.updateObject(role);
			// 先删除角色与菜单关系
			this.sysRoleMenuDao.deleteObjects(role.getId());
			// 再添加角色与菜单的关系
			this.sysRoleMenuDao.insertObject(role.getId(), menuIds);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("服务器正在维护中");
		}
		return rows;
	}

	@Override
	public int insertObject(SysRole role, Integer[] menuIds) {
		if (role == null) {
			throw new ServiceException("新增的对象不能为空");
		}
		if (StringUtils.isEmpty(role.getName())) {
			throw new ServiceException("角色名不能为空");
		}
		if (StringUtils.isEmpty(menuIds))
			throw new ServiceException("请为该角色选择权限");

		// 设置创建用户
		String modifiedUser = ShiroUtils.getPrincipal().getUsername();
		role.setModifiedUser(modifiedUser);
		role.setCreatedUser(modifiedUser);

		// 将角色信息插入到数据库
		int rows = 0;
		try {
			rows = this.sysRoleDao.insertObject(role);
			// 将角色与菜单的关系插入到数据库
			this.sysRoleMenuDao.insertObject(role.getId(), menuIds);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("服务器正在维护中");
		}

		return rows;
	}

	@Override
	public List<CheckBox> findObjects() {
		return this.sysRoleDao.findAllObjects();
	}
}
