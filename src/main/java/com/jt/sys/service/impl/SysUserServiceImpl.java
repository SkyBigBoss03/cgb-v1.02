package com.jt.sys.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.code.kaptcha.Constants;
import com.jt.sys.common.exception.ServiceException;
import com.jt.sys.dao.SysUserDao;
import com.jt.sys.dao.SysUserRoleDao;
import com.jt.sys.entity.SysUser;
import com.jt.sys.service.SysUserService;
import com.jt.sys.service.realm.CustomToken;
import com.jt.sys.service.realm.LoginType;
import com.jt.sys.util.ExportDBUtil;
import com.jt.sys.util.ShiroUtils;

@Service
public class SysUserServiceImpl implements SysUserService {
	@Autowired
	private SysUserDao sysUserDao;
	@Autowired
	private SysUserRoleDao sysUserRoleDao;

	private SysUser commonLogin(String username, String password, String rememberMe, String captext, String loginType) {
		if (StringUtils.isEmpty(username))
			throw new ServiceException("用户名不能为空");
		if (StringUtils.isEmpty(password))
			throw new ServiceException("密码不能为空");
		if (StringUtils.isEmpty(captext))
			throw new ServiceException("验证码不能为空");
		
		String sessionCaptext = (String) ShiroUtils.getSessionValue(Constants.KAPTCHA_SESSION_KEY);
		if (StringUtils.isEmpty(sessionCaptext))
			throw new ServiceException("验证码无效，请重新生成");
		if (!captext.toLowerCase().equals(sessionCaptext.toLowerCase()))
			throw new ServiceException("验证码输入不正确");

		// 获取主体对象
		Subject subject = SecurityUtils.getSubject();
		// 封装用户名密码的token对象
		CustomToken token = new CustomToken(username, password, rememberMe, captext, loginType);
		// 开始执行身份认证
		// 提交请求道sercurityManager，再通过securityManager调用认证处理器Authenticator
		// 认证处理器再调用对应的realm对象获取认证信息
		subject.login(token);
		// 认证通过后，保存认证信息
//		Session session = SecurityUtils.getSubject().getSession();
//		session.setAttribute("user", username);
		
		SysUser userMap = (SysUser)subject.getPrincipals().getPrimaryPrincipal(); 
		return userMap;
	}

	@Override
	public SysUser userLogin(String username, String password, String rememberMe, String captext) {
		return commonLogin(username, password, rememberMe, captext, LoginType.USER.toString());
	}

	@Override
	public SysUser adminLogin(String username, String password, String rememberMe, String captext) {
		return commonLogin(username, password, rememberMe, captext, LoginType.ADMIN.toString());
	}

	@Override
	public PageInfo<SysUser> findPageObjects(Integer currentPage, String username) {
		if (currentPage == null || currentPage < 1) {
			throw new ServiceException("页码数据不正确");
		}

		/*
		int pageSize = 3;
		int startIndex = (currentPage - 1) * pageSize;

		List<SysUser> list = this.sysUserDao.findPageObjects(startIndex, pageSize, username);
		int rows = this.sysUserDao.getRowCounts(username);
		PageObject<SysUser> pageObject = new PageObject<>();
		pageObject.setPageCurrent(currentPage);
		pageObject.setPageSize(pageSize);
		pageObject.setRecords(list);
		pageObject.setRowCount(rows);
		*/
		
		PageHelper.startPage(currentPage, 3);
		List<SysUser> list = this.sysUserDao.findObjects(username);
		PageInfo<SysUser> pageObject = new PageInfo<>(list);
		return pageObject;
	}

	@RequiresPermissions("sys:user:update")
//	@RequiresRoles("")
	@Override
	public int validById(Integer id, Integer valid, String modifiedUser) {
		if (id == null || id < 1)
			throw new ServiceException("参数不合法id=" + id);
		if (valid != 1 && valid != 0)
			throw new ServiceException("参数不合法valid=" + valid);
		// if(StringUtils.isEmpty(modifiedUser))
		// throw new ServiceException("修改用户不能为空");

		int rows = 0;
		try {
			rows = this.sysUserDao.validById(id, valid, modifiedUser);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("服务器正在维护中");
		}
		if (rows == 0) {
			throw new ServiceException("此记录可能已经不存在");
		}
		return rows;
	}

	@Override
	public int saveObject(SysUser sysUser, String[] roleIds) {
		if (sysUser == null || roleIds == null)
			throw new ServiceException("参数不能为空");
		if (StringUtils.isEmpty(sysUser.getUsername()))
			throw new ServiceException("用户名不能为空");
		if (StringUtils.isEmpty(sysUser.getPassword()))
			throw new ServiceException("密码不能为空");

		int users = this.sysUserDao.getRowCounts(sysUser.getUsername());
		if (users > 0) {
			throw new ServiceException("用户名已存在");
		}

		// 设置创建用户
		String createdUser = ShiroUtils.getPrincipal().getUsername();
		sysUser.setCreatedUser(createdUser);
		sysUser.setModifiedUser(createdUser);

		if (sysUser.getValid() == null) {
			sysUser.setValid(0);
		}
		// 生成随机salt
		String salt = UUID.randomUUID().toString();
		sysUser.setSalt(salt);
		// 将密码进行加密
		SimpleHash sh = new SimpleHash("MD5", sysUser.getPassword(), salt);
		sysUser.setPassword(sh.toString());

		int rows = 0;
		try {
			rows = this.sysUserDao.insertObject(sysUser);
			this.sysUserRoleDao.insertObject(sysUser.getId(), roleIds);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("服务器正在维护中");
		}
		return rows;
	}

	@Override
	public Map<String, Object> findObjectById(Integer id) {
		if (id == null || id < 1)
			throw new ServiceException("参数不合法id=" + id);

		SysUser user = this.sysUserDao.findObjectById(id);
		if (user == null)
			throw new ServiceException("用户不存在");

		List<Integer> roleIds = this.sysUserRoleDao.findRoleIdsByUserId(id);

		Map<String, Object> map = new HashMap<>();
		map.put("user", user);
		map.put("roleIds", roleIds);

		return map;
	}

	@Override
	public int updateObject(SysUser sysUser, String[] roleIds) {
		if (sysUser == null || roleIds == null)
			throw new ServiceException("参数不能为空");
		if (sysUser.getId() == null || sysUser.getId() < 1)
			throw new ServiceException("用户id不能为空");
		if (StringUtils.isEmpty(sysUser.getUsername()))
			throw new ServiceException("用户名不能为空");
		if (StringUtils.isEmpty(sysUser.getPassword()))
			throw new ServiceException("密码不能为空");

		// 设置创建用户
		String modifiedUser = ShiroUtils.getPrincipal().getUsername();
		sysUser.setModifiedUser(modifiedUser);

		// 生成随机salt
		String salt = UUID.randomUUID().toString();
		sysUser.setSalt(salt);
		// 将密码进行加密
		SimpleHash sh = new SimpleHash("MD5", sysUser.getPassword(), salt);
		sysUser.setPassword(sh.toString());

		int rows = 0;
		try {
			rows = this.sysUserDao.updateObject(sysUser);
			this.sysUserRoleDao.deleteObject(sysUser.getId());
			this.sysUserRoleDao.insertObject(sysUser.getId(), roleIds);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("服务器正在维护中");
		}
		if (rows == 0)
			throw new ServiceException("修改的数据可能已不存在");
		return rows;
	}

	@RequiresPermissions("sys:user:view")
	@Override
	public void exportExcelUsers(HttpServletResponse response) {
		List<SysUser> users = this.sysUserDao.findAllObjects();
		
		List<String> tableHeaders = this.sysUserDao.findTableColumns("sys_users");
		try {
			ExportDBUtil.exportExcel("用户信息", tableHeaders, users, response);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException("文件导出失败");
		}
	}

	@Override
	public void exportPDFUsers(HttpServletResponse response) {
		List<SysUser> users = this.sysUserDao.findAllObjects();

		List<String> tableHeaders = this.sysUserDao.findTableColumns("sys_users");
		try {
			ExportDBUtil.exportPDF("用户信息", tableHeaders, users, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("文件导出失败");
		}
	}
}
