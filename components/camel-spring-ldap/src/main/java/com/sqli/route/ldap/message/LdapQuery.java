package com.sqli.route.ldap.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class LdapQuery extends Query {
	private static final long serialVersionUID = 1L;

	//<=>datasource
	private String ldapTemplate;
	//serait mieux d'avoir un context source (plus de dépendance directe sur Spring)
	
	//<=> statement
	private String base;
	private String filter;
	
	// private String attributes;
	/** SearchControls.OBJECT_SCOPE / ONELEVEL_SCOPE / SUBTREE_SCOPE */
	private int scope;
	
	public enum Scope {
		OBJECT, ONELEVEL, SUBTREE
	}
}
