package com.onlymaker.leo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @see [http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-security]
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    PasswordAuthenticationProvider passwordAuthenticationProvider;

    /*
    * If defined the default authenticationManager will be disabled
    */
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(passwordAuthenticationProvider);
    }

    /*
     * If defined the default web security will be disabled
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/", "/index", "/create").hasRole("CUSTOMER")
                .anyRequest().permitAll()
                .and().httpBasic();
    }
}
