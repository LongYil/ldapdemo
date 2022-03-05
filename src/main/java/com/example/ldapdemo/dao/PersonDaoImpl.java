/*
 * Copyright 2005-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.ldapdemo.dao;


import com.example.ldapdemo.domain.HcUser;
import com.example.ldapdemo.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.*;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

/**
 * Default implementation of PersonDao. This implementation uses
 * DirContextAdapter for managing attribute values. We use a ContextMapper
 * to map from the found contexts to our domain objects. This is especially useful
 * since we in this case have properties in our domain objects that depend on parts of the DN.
 * <p>
 * We could have worked with Attributes and an AttributesMapper implementation
 * instead, but working with Attributes is a bore and also, working with
 * AttributesMapper objects (or, indeed Attributes) does not give us access to
 * the distinguished name. However, we do use it in one method that only needs a
 * single attribute: {@link #getAllPersonNames()}.
 *
 * @author Mattias Hellborg Arthursson
 * @author Ulrik Sandberg
 */
@Component
public class PersonDaoImpl implements PersonDao {

    @Autowired
    private LdapTemplate ldapTemplate;

    private final String BASE_DN = "dc=springframework,dc=org";

    @Override
    public List<String> getPersonNamesByLastName(String lastName, String password) {
        LdapQuery query = query()
                .base("dc=springframework,dc=org")
                .attributes("cn", "sn")
                .where("objectclass").is("person")
                .and("sn").is(lastName)
                .and("userPassword").is(password);

        return ldapTemplate.search(query,
                new AttributesMapper<String>() {
                    public String mapFromAttributes(Attributes attrs)
                            throws NamingException {
                        return (String) attrs.get("cn").get();
                    }
                });
    }


    @Override
    public void update(Person person) {
        Name dn = buildDn(person);
        DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
        mapToContext(person, context);
        ldapTemplate.modifyAttributes(dn, context.getModificationItems());
    }

    @Override
    public void delete(Person person) {
        ldapTemplate.unbind(buildDn(person));
    }

    @Override
    public List<String> getAllPersonNames() {
        return ldapTemplate.search(
                query().base("dc=springframework,dc=org")
                        .where("objectclass").is("person"),
                new AttributesMapper<String>() {
                    public String mapFromAttributes(Attributes attrs)
                            throws NamingException {
                        return (String) attrs.get("cn").get();
                    }
                });
    }

    @Override
    public List<Person> findAll() {
        return ldapTemplate.search(query()
                        .where("objectclass").is("person"),
                PERSON_CONTEXT_MAPPER);
    }

    @Override
    public Person findByPrimaryKey(String country, String company, String fullname) {
        LdapName dn = buildDn(country, company, fullname);
        return ldapTemplate.lookup(dn, PERSON_CONTEXT_MAPPER);
    }

    private void mapToContext(Person person, DirContextAdapter context) {
        context.setAttributeValues("objectclass", new String[]{"top", "person"});
//		context.setAttributeValue("cn", person.getFullName());
        context.setAttributeValue("sn", person.getLastName());
//		context.setAttributeValue("description", person.getDescription());
        context.setAttributeValue("telephoneNumber", person.getPhone());
    }

    /**
     * Maps from DirContextAdapter to Person objects. A DN for a person will be
     * of the form <code>cn=[fullname],ou=[company],c=[country]</code>, so
     * the values of these attributes must be extracted from the DN. For this,
     * we use the LdapName along with utility methods in LdapUtils.
     */
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


    private class PersonAttributesMapper implements AttributesMapper<Person> {
        public Person mapFromAttributes(Attributes attrs) throws NamingException {
            Person person = new Person();
            person.setFullName((String) attrs.get("cn").get());
            person.setLastName(attrs.get("sn") == null ? "" : (String) attrs.get("sn").toString());
            return person;
        }
    }

    public List<Person> getAllPersons() {
        return ldapTemplate.search(query().base("dc=springframework,dc=org")
                .where("objectclass").is("person"), new PersonAttributesMapper());
    }

    public Person findPerson(String dn) {
        return ldapTemplate.lookup(dn, new PersonAttributesMapper());
    }

    protected Name buildPersonQuery(Person p) {
        return LdapNameBuilder.newInstance(BASE_DN)
                .add("cn", p.getCompany())
                .build();
    }

    protected Person buildPerson(Name dn, Attributes attrs) {
        Person person = new Person();
        person.setCountry(LdapUtils.getStringValue(dn, "cn"));
        person.setCompany(LdapUtils.getStringValue(dn, "ou"));
        return person;
    }

    public void create(Person p) {
//        Person person = new Person();
//        Name dn = buildDn(person);
        Name dn = LdapNameBuilder.newInstance("dc=springframework,dc=org")
//                .add("sn", "li")
                .add("ou", "cloudnative")
                .add("cn", "lixiaolong")
                .build();

        DirContextAdapter context = new DirContextAdapter(dn);
        context.setAttributeValues("objectclass", new String[]{"top", "person"});
        mapToContext(p, context);
        ldapTemplate.bind(context);
    }

    private Attributes buildAttributes(Person p) {
        Attributes attrs = new BasicAttributes();
        BasicAttribute ocattr = new BasicAttribute("objectclass");
        ocattr.add("top");
        ocattr.add("person");
        attrs.put(ocattr);
        attrs.put("cn", "lylong");
        attrs.put("ou", "cloudnative");
        return attrs;
    }

    private LdapName buildDn(Person person) {
        return buildDn(person.getCountry(), person.getCompany(), person.getFullName());
    }

    private LdapName buildDn(String country, String company, String fullname) {
        return LdapNameBuilder.newInstance(BASE_DN)
                .add("sn", "li")
                .add("ou", "cloudnative")
                .add("cn", "zhuge")
                .build();
    }

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }
}
