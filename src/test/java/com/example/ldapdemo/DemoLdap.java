package com.example.ldapdemo;

/**
 * @author liyinlong
 * @since 2022/3/4 10:12 上午
 */

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

public class DemoLdap {

    public void JNDILookup() {
        String rootFilter = "o=springframework,o=org";
//        String filter = "(&(smart-type=E1)(smart-status=1))";
        String filter = "(&(smart-type=E1)(uid=00012047))";
        String username = "cn=admin,ou=springframework,dc=org";//xxx为申请的对接账户
        String password = "Hc@Cloud01";

        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");//设置连接LDAP的实现工厂
        env.put(Context.PROVIDER_URL, "ldap://10.10.101.140:1389/" + rootFilter);// 指定LDAP服务器的主机名和端口号
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//给环境提供认证方法,有SIMPLE、SSL/TLS和SASL
        env.put(Context.SECURITY_PRINCIPAL, username);//指定进入的目录识别名DN
        env.put(Context.SECURITY_CREDENTIALS, password); //进入的目录密码
        env.put("filter",filter);
        DirContext ctx = null;

        try {
            // 得到初始目录环境的一个引用
            ctx = new InitialDirContext(env);

            //The search base entry 'uid=00012047,ou=Internal,ou=People,o=xxx.com,o=isp' does not exist]; remaining name 'uid=00012047,ou=Internal'
//            Attributes attrs = ctx.getAttributes("uid=00012047,ou=Internal,ou=People");//获取到一个人员，


            NamingEnumeration bindings = ctx.listBindings("ou=Internal,ou=People");//列举 内部人员

            while (bindings.hasMore()) {
                Binding bd = (Binding)bindings.next();
                System.out.println(bd.getName() + ": " + bd.getObject());
            }


            /*根据结点的DN来查找它的所有属性, 然后再从属性中得到所有的值,注意一个属性可以有多个值*/
//            for (NamingEnumeration ae = attrs.getAll(); ae.hasMore(); ) {
//                //获取一个属性
//                Attribute attr = (Attribute) ae.next();
//                for (NamingEnumeration ve = attr.getAll(); ve.hasMore(); ) {
//                    System.out.println(String.format("Attribute=%s,Value=%s",attr.getID(),ve.next()) );
//                }
//            }

        } catch (javax.naming.AuthenticationException e) {
            System.out.println("认证失败");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("认证出错：");
            e.printStackTrace();
        }finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void main(String[] args) {
        DemoLdap ldapJNDI = new DemoLdap();
        ldapJNDI.JNDILookup();

    }

}



