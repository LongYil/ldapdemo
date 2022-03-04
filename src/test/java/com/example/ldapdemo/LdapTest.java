package com.example.ldapdemo;

import com.example.ldapdemo.domain.Person;
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

    private final static ContextMapper<Person> PERSON_CONTEXT_MAPPER = new AbstractContextMapper<Person>() {
        @Override
        public Person doMapFromContext(DirContextOperations context) {
            Person person = new Person();

            LdapName dn = LdapUtils.newLdapName(context.getDn());
            person.setCountry(LdapUtils.getStringValue(dn, 0));
            person.setCompany(LdapUtils.getStringValue(dn, 1));
            person.setFullName(context.getStringAttribute("cn"));
            person.setLastName(context.getStringAttribute("sn"));
            person.setDescription(context.getStringAttribute("description"));
            person.setPhone(context.getStringAttribute("telephoneNumber"));

            return person;
        }
    };

    public Person getPerson(){
        LdapTemplate template = getTemplate();
        LdapName dn = buildDn("users");
        return template.lookup(dn, PERSON_CONTEXT_MAPPER);
    }

    private LdapName buildDn(Person person) {
        return buildDn(person.getCompany());
    }

    private LdapName buildDn(String department) {
        return LdapNameBuilder.newInstance()
                .add("ou", department)
                .build();
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
        List<String> children = sClient.getListing();

        for (String child : children) {
            System.out.println(child);
        }

    }

}