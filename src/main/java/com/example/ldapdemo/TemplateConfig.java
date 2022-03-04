package com.example.ldapdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * @author liyinlong
 * @since 2022/3/4 9:22 上午
 */
//@Configuration
public class TemplateConfig {

    @Bean
    public LdapContextSource ldapContextSource() {
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl("ldap://10.10.101.140:1389");
        ldapContextSource.setBase("dc=springframework,dc=org");
        ldapContextSource.setPassword("Hc@Cloud01");
        return ldapContextSource;
    }


    @Bean
    // LdapTemplate：连接LDAP库
    public LdapTemplate ldapTemplate() {
        LdapTemplate ldapTemplate = new LdapTemplate();
        ldapTemplate.setContextSource(ldapContextSource());
        return ldapTemplate;
    }

}
