package com.onlymaker.leo.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by jibo on 2016/10/27.
 */
abstract class BaseUserDetails implements UserDetails {
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
