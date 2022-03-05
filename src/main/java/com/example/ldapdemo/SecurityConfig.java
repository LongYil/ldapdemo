package com.example.ldapdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.server.UnboundIdContainer;
import org.springframework.security.ldap.userdetails.PersonContextMapper;

import java.util.List;

@Configuration
public class SecurityConfig {

//    @Bean
//    LdapTemplate ldapTemplate() {
//        LdapContextSource contextSource = new LdapContextSource();
//        contextSource.setUrl("ldap://10.10.101.140:1389");
//        contextSource.setUserDn("cn=admin,dc=springframework,dc=org");
//        contextSource.setPassword("Hc@Cloud01");
//        try {
//            contextSource.afterPropertiesSet();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
//        List<String> list = ldapTemplate.list("ou=cloudnative,dc=springframework,dc=org");
//        list.forEach(item -> {
//            System.out.println(item);
//        });
//        return ldapTemplate;
//    }

    @Bean
    BindAuthenticator authenticator(BaseLdapPathContextSource contextSource) {
        BindAuthenticator authenticator = new BindAuthenticator(contextSource);
        authenticator.setUserDnPatterns(new String[] { "uid={0},ou=cloudnative" });
        return authenticator;
    }

    @Bean
    LdapAuthenticationProvider authenticationProvider(LdapAuthenticator authenticator) {
        LdapAuthenticationProvider provider = new LdapAuthenticationProvider(authenticator);
        provider.setUserDetailsContextMapper(new PersonContextMapper());
        return provider;
    }
}
