package com.example.ldapdemo;

import com.example.ldapdemo.domain.HcUser;
import com.example.ldapdemo.domain.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;

import javax.naming.ldap.LdapName;
import java.util.List;

public class LdapTest {


    public List<String> getListing() {

        LdapTemplate template = getTemplate();

        List<String> children = template.list("ou=cloudnative,dc=springframework,dc=org");

        return children;
    }

    public void save(){
        LdapTemplate template = getTemplate();
        HcUser hcUser = new HcUser();
        hcUser.setFullName("hahahah");
        hcUser.setUid("lllii");

        template.delete(hcUser);
    }


    private LdapTemplate getTemplate() {

        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl("ldap://10.10.101.140:1389");
        contextSource.setUserDn("cn=admin,dc=springframework,dc=org");
        contextSource.setPassword("Hc@Cloud01");

        try {
            contextSource.afterPropertiesSet();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        LdapTemplate template = new LdapTemplate();

        template.setContextSource(contextSource);

        return template;

    }


    public static void main(String[] args) {

        LdapTest sClient = new LdapTest();
        sClient.save();
//        List<String> children = sClient.getListing();
//
//        for (String child : children) {
//            System.out.println(child);
//        }

    }

}