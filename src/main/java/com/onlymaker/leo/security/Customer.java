package com.onlymaker.leo.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

/**
 * Created by jibo on 2016/10/27.
 */
public class Customer extends BaseUserDetails {
    public static final String username = "admin";
    public static final String password = "leo20161018";

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return  AuthorityUtils.createAuthorityList("ROLE_CUSTOMER");
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
