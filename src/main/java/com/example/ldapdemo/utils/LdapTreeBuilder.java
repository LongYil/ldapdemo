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

package com.example.ldapdemo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Component;

import javax.naming.Name;

@Component
public class LdapTreeBuilder {

    @Autowired
    private LdapTemplate ldapTemplate;

    public LdapTreeBuilder(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public LdapTree getLdapTree(Name root) {
        DirContextOperations context = ldapTemplate.lookupContext(root);
        return getLdapTree(context);
    }

    private LdapTree getLdapTree(final DirContextOperations rootContext) {
        final LdapTree ldapTree = new LdapTree(rootContext);
        ldapTemplate.listBindings(rootContext.getDn(),
                new AbstractContextMapper<Object>() {
                    @Override
                    protected Object doMapFromContext(DirContextOperations ctx) {
                        Name dn = ctx.getDn();
                        dn = LdapUtils.prepend(dn, rootContext.getDn());
                        ldapTree.addSubTree(getLdapTree(ldapTemplate
                                .lookupContext(dn)));
                        return null;
                    }
                });
        return ldapTree;
    }


}
