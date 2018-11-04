package com.jt.sys.service.realm;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

public class CustomModularRealmAuthenticator extends ModularRealmAuthenticator {
	@Override
	protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken) {
		assertRealmsConfigured();
		Collection<Realm> realms = getRealms();
		Collection<Realm> typeRealms = new ArrayList<>();

		CustomToken token = (CustomToken)authenticationToken;
		
		for (Realm realm : realms) {
			// 重新定义认证的realm
			if(realm.getName().contains(token.getLoginType()))
				typeRealms.add(realm);
		}
		
		if (typeRealms.size() == 1) {
			return doSingleRealmAuthentication(typeRealms.iterator().next(), token);
		} else {
			return doMultiRealmAuthentication(typeRealms, token);
		}
	}

}
