package com.sqli.route.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

import com.sqli.route.ldap.message.LdapQuery;
import com.sqli.route.ldap.message.Query;

public class LdapProcessor implements Processor {

	private AttributesMapper<Attributes> mapper = new AttributesMapper<Attributes>() {
		@Override
		public Attributes mapFromAttributes(Attributes attributes) throws NamingException {
			return attributes;
		}
	};

	@Override
	public void process(Exchange exchange) throws Exception {
		LdapQuery query = exchange.getIn().getBody(LdapQuery.class);
		// TODO context source (+ générique, API Java JNDI directement)
		LdapTemplate ldapTemplate = exchange.getContext().getRegistry().lookupByNameAndType(query.getLdapTemplate(), LdapTemplate.class);
		Object result = null;
		if (query.getReturnType().equals(Query.ReturnType.LIST_OF_JAVA_CLASS)) {
			result = ldapTemplate.findAll(new LdapName(query.getBase()), new SearchControls(), query.getReturnTypeClass());
			//TODO
			//mapper perso aussi ?
		} else if (query.getReturnType().equals(Query.ReturnType.JAVA_CLASS)) {
			result = ldapTemplate.findByDn(new LdapName(query.getBase()), query.getReturnTypeClass());
			//TODO
		} else {
			throw new UnsupportedOperationException("TODO Query pas bonne");
		}
		ldapTemplate.search(query.getBase(), query.getFilter(), query.getScope(), mapper);
		exchange.getIn().setBody(result);

	}
}
