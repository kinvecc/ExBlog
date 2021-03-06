package com.nanyin.config.shiroConfig;


import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;

/**
 * Created by NanYin on 2017-09-23 下午10:27.
 * 包名： com.nanyin.config.shiroConfig
 * 类描述：shiro 的配置类
 */
@Configuration
public class ShiroConfig {
    @Bean(name="shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager") SecurityManager manager) {
        ShiroFilterFactoryBean bean=new ShiroFilterFactoryBean();
        bean.setSecurityManager(manager);
        //配置登录的url和登录成功的url
        bean.setLoginUrl("/user/login");
        bean.setUnauthorizedUrl("/error/unAuthorized");
        //配置访问权限
        LinkedHashMap<String, String> filterChainDefinitionMap=new LinkedHashMap<>();
        /*
         * 第一部分 前台无权限url
         */
        filterChainDefinitionMap.put("/main/**", "anon"); //表示可以匿名访问
        filterChainDefinitionMap.put("/error/**", "anon"); //表示可以匿名访问

//      静态可访问资源
        filterChainDefinitionMap.put("/Error/*", "anon");
        filterChainDefinitionMap.put("/user/*", "anon"); //表示可以匿名访问
        filterChainDefinitionMap.put("/home", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/images/**", "anon");
        filterChainDefinitionMap.put("/editmd/**", "anon");
        /*
         * 需要特殊权限的url 如管理员设置
         */
//          专题和专题插入删除
        filterChainDefinitionMap.put("/column/columnPage", "roles[admin]");
        filterChainDefinitionMap.put("/column/insertInlet", "roles[admin]");
        filterChainDefinitionMap.put("/column/deleteColumn", "roles[admin]");
        filterChainDefinitionMap.put("/column/editColumn", "roles[admin]");
//        管理源文章审核
        filterChainDefinitionMap.put("/paper/adminPapers", "roles[admin]");
        filterChainDefinitionMap.put("/paper/FixPaperStatues/*", "roles[admin]");
        filterChainDefinitionMap.put("/paper/updatePaperStatus", "roles[admin]");
        filterChainDefinitionMap.put("/paper/admin", "roles[admin]");
//       人员管理（未完成）

//       其他设置user 即登录和rememberMe都可直接操作的
        filterChainDefinitionMap.put("/*", "user");//表示需要认证才可以访问
        filterChainDefinitionMap.put("/**", "user");
        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return bean;
    }
    //配置核心安全事务管理器
    @Bean(name="securityManager")
    public SecurityManager securityManager(@Qualifier("authRealm") MyRaalm authRealm) {
        System.err.println("--------------shiro已经加载----------------");
        DefaultWebSecurityManager manager=new DefaultWebSecurityManager();
        manager.setRealm(authRealm);
        manager.setRememberMeManager(rememberMeManager());
        return manager;
    }
    //配置自定义的权限登录器
    @Bean(name="authRealm")
    public MyRaalm myRaalm() {
        MyRaalm authRealm=new MyRaalm();
        return authRealm;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator creator=new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") SecurityManager manager) {
        AuthorizationAttributeSourceAdvisor advisor=new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(manager);
        return advisor;
    }

    @Bean
    public SimpleCookie SimpleCookie(){
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(2592000);//30天
        return simpleCookie;
    }

    @Bean
    public CookieRememberMeManager rememberMeManager(){
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCipherKey(Base64.decode("2AvVhdsgUs0FSA3SDFAdag=="));
        cookieRememberMeManager.setCookie(SimpleCookie());
        return cookieRememberMeManager;
    }
}
