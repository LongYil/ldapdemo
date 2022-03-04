package com.example.ldapdemo.controller;

import com.example.ldapdemo.dao.PersonDaoImpl;
import com.example.ldapdemo.domain.Person;
import com.example.ldapdemo.utils.HtmlRowLdapTreeVisitor;
import com.example.ldapdemo.utils.LdapTree;
import com.example.ldapdemo.utils.LdapTreeBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.naming.Name;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Default controller.
 * 
 * @author Mattias Hellborg Arthursson
 */
@RestController
public class DefaultController {

	@Autowired
	private LdapTreeBuilder ldapTreeBuilder;

	@Autowired
	private PersonDaoImpl personDao;

	@GetMapping("/welcome.do")
	public void welcomeHandler() {
	}

	@GetMapping("/showTree.do")
	public ModelAndView showTree() {
		LdapTree ldapTree = ldapTreeBuilder.getLdapTree(LdapUtils.emptyLdapName());
		HtmlRowLdapTreeVisitor visitor = new PersonLinkHtmlRowLdapTreeVisitor();
		ldapTree.traverse(visitor);
		return new ModelAndView("showTree", "rows", visitor.getRows());
	}

	@GetMapping("/addPerson.do")
	public String addPerson() {
		Person person = getPerson();

		personDao.create(person);
		return "redirect:/showTree.do";
	}

	@GetMapping("/updatePhoneNumber.do")
	public String updatePhoneNumber() {
		Person person = personDao.findByPrimaryKey("Sweden", "company1", "John Doe");
		person.setPhone(StringUtils.join(new String[]{person.getPhone(), "0"}));
		personDao.update(person);
		return "redirect:/showTree.do";
	}

	@RequestMapping("/removePerson.do")
	public String removePerson() {
		Person person = getPerson();

		personDao.delete(person);
        return "redirect:/showTree.do";
	}

	@RequestMapping("/showPerson.do")
	public ModelMap showPerson(String country, String company, String fullName) {
		Person person = personDao.findByPrimaryKey(country, company, fullName);
		return new ModelMap("person", person);
	}

	private Person getPerson() {
		Person person = new Person();
		person.setFullName("Li Yinlong");
		person.setLastName("Yinlong");
		person.setCompany("people");
//		person.setCountry("Sweden");
		person.setDescription("Test user");
		return person;
	}

	/**
	 * Generates appropriate links for person leaves in the tree.
	 * 
	 * @author Mattias Hellborg Arthursson
	 */
	private static final class PersonLinkHtmlRowLdapTreeVisitor extends HtmlRowLdapTreeVisitor {
		@Override
		protected String getLinkForNode(DirContextOperations node) {
			String[] objectClassValues = node.getStringAttributes("objectClass");
			if (containsValue(objectClassValues, "person")) {
				Name dn = node.getDn();
				String country = encodeValue(LdapUtils.getStringValue(dn, "c"));
				String company = encodeValue(LdapUtils.getStringValue(dn, "ou"));
				String fullName = encodeValue(LdapUtils.getStringValue(dn, "cn"));

				return "showPerson.do?country=" + country + "&company=" + company + "&fullName=" + fullName;
			}
			else {
				return super.getLinkForNode(node);
			}
		}

		private String encodeValue(String value) {
			try {
				return URLEncoder.encode(value, "UTF8");
			}
			catch (UnsupportedEncodingException e) {
				// Not supposed to happen
				throw new RuntimeException("Unexpected encoding exception", e);
			}
		}

		private boolean containsValue(String[] values, String value) {
			for (String oneValue : values) {
				if (StringUtils.equals(oneValue, value)) {
					return true;
				}
			}
			return false;
		}
	}

}
