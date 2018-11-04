package com.jt.sys.service.realm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.jt.sys.common.exception.CustomAuthUserException;
import com.jt.sys.dao.SysUserDao;
import com.jt.sys.entity.SysUser;

public class ShiroUserRealm extends AuthorizingRealm {

	@Autowired
	private SysUserDao sysUserDao;
	
	/**
	 * 授权操作
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// 获取用户登录信息
		SysUser user = (SysUser)principals.getPrimaryPrincipal();
		// 查询用户的权限信息
		List<String> permissions = this.sysUserDao.findUserPermissions(user.getId());
		// 去重用户重复的权限信息
		Set<String> permissionSet = new HashSet<>();
		for (String string : permissions) {
			if(!StringUtils.isEmpty(string)) {
				permissionSet.add(string);
			}
		}
		// 对用户权限进行封装
		SimpleAuthorizationInfo authorizeInfo = new SimpleAuthorizationInfo();
		authorizeInfo.setStringPermissions(permissionSet);
		return authorizeInfo;
	}

	/**
	 * 认证操作
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
		// 将AuthenticationToken转换成其实现类UsernamePasswordToken
		CustomToken upassToken = (CustomToken)token;
		// 通过token获取用户名
		String username = upassToken.getUsername();
		// 判断用户名是否为空
		if(StringUtils.isEmpty(username))
			throw new CustomAuthUserException("用户名不能为空");
		// 通过用户名获取用户信息
		SysUser user = (SysUser)this.sysUserDao.findObjectByUsername(username);
		// 如果没有获取到用户信息，抛出异常
		if(user == null)
			throw new CustomAuthUserException("用户不存在");
		if(user.getValid()==0)
			throw new CustomAuthUserException("用户已被禁用，请联系管理员");
		// 通过密码及盐值创建MD5加密后的对象
		ByteSource bs = ByteSource.Util.bytes(user.getSalt());
		// 创建认证信息对象，传入用户名、密码、盐值加密的对象
		SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user, user.getPassword(), bs, getName());
		return authenticationInfo;
	}
}















