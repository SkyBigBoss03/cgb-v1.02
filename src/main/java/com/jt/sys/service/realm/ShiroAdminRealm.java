package com.jt.sys.service.realm;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.jt.sys.common.exception.CustomAuthUserException;
import com.jt.sys.dao.SysUserDao;
import com.jt.sys.entity.SysUser;

public class ShiroAdminRealm extends AuthenticatingRealm {
	@Autowired
	private SysUserDao sysUserDao;
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
		// 将AuthenticationToken转换成其实现类UsernamePasswordToken
		CustomToken upassToken = (CustomToken)token;
		// 通过token获取用户名
		String username = upassToken.getUsername();
		// 判断用户名是否为空
		if(StringUtils.isEmpty(username))
			throw new CustomAuthUserException("用户名不能为空");
		// 通过用户名获取是否为管理用户
		SysUser user = (SysUser)this.sysUserDao.findAdminByUsername(username);
		// 如果没有获取到用户信息，抛出异常
		if(user == null)
			throw new CustomAuthUserException("非管理员禁止登录");
		// 通过密码及盐值创建MD5加密后的对象
		ByteSource bs = ByteSource.Util.bytes(user.getSalt());
		// 创建认证信息对象，传入用户名、密码、盐值加密的对象
		SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user, user.getPassword(), bs, getName());
		return authenticationInfo;
	}
}




















