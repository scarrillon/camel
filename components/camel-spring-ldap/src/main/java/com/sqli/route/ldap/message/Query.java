package com.sqli.route.ldap.message;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

@Data
public class Query implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum Type {
		SQL
	}
	public static final class ReturnType {
		/** Pas de retour attendu */
		public static final String EMPTY = "empty";
		/** Retour du composant renvoyé tel quel, aucune transformation n'est réalisée */
		public static final String RAW = "raw";
		/** Retour converti en string json */
		public static final String JSON = "json";
		/** Raccourci pour returnType = javaClass & returnTypeClass = String.class */
		public static final String STRING = "string";
		/** Si returnType = javaClass, returnTypeClass doit être renseigné */
		public static final String JAVA_CLASS = "javaClass";
		/** Si returnType = listOfJavaClass, returnTypeClass doit être renseigné */
		public static final String LIST_OF_JAVA_CLASS = "listOfJavaClass";
		private ReturnType() {
		}
	}
	
	private String datasource;
	private Query.Type type;
	private String statement;
	private Object parameters; //Map ou Iterable (Array/Collection ok) ou String avec virgules, TODO documenter
	private String returnType = ReturnType.RAW;
	private Class<?> returnTypeClass;
	private Map<String, String> otherOptions;
	
	public Query() {
	}
	
	/**
	 * Copy constructor
	 */
	protected Query(Query query) {
		this.datasource = query.datasource;
		this.type = query.type;
		this.statement = query.statement;
		this.parameters = query.parameters;
		this.returnType = query.returnType;
		this.returnTypeClass = query.returnTypeClass;
		this.otherOptions = query.otherOptions;
	}
	
	public void checkStateLegality() throws IllegalStateException {
		if (datasource == null || type == null || statement == null || returnType == null) {
			throw new IllegalStateException("datasource, type, statement, returnType must not be null");
		}
		checkReturnTypeValidity();
	}
	
	/**
	 * Vérifie que les champs returnType & returnTypeClass sont bien renseignés
	 * Lance une IllegalStateException s'il y a un problème.
	 * @throws IllegalStateException
	 */
	public void checkReturnTypeValidity() throws IllegalStateException {
		if (returnType == null) {
			throw new IllegalStateException("returnType is null: " + this);
		}
		if (returnType.equals(ReturnType.JAVA_CLASS) 
				|| returnType.equals(ReturnType.LIST_OF_JAVA_CLASS)) {
			if (returnTypeClass == null) {
				throw new IllegalStateException("invalid return type, missing returnTypeClass for " + this);
			}
		} else if (returnTypeClass != null) {
			throw new IllegalStateException("returnTypeClass is set but will be ignored since returnType is not javaClass nor listOfJavaClasses: " + this);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Factory pour renvoyer des Query ou QueryBuilder pré-remplis
	 */
	public static class QueryFactory {
		private final Query baseQuery;

		/**
		 * Retourne une Factory paramétrée pour renvoyer des copies de baseQuery
		 * @param baseQuery
		 */
		public QueryFactory(Query baseQuery) {
			this.baseQuery = new Query(baseQuery);
		}
		/**
		 * Retourne une Factory de Query pré-configurées
		 * @param datasource
		 * @param type
		 */
		public QueryFactory(String datasource, Type type) {
			baseQuery = new Query();
			baseQuery.setDatasource(datasource);
			baseQuery.setType(type);
		}
		/**
		 * Retourne une Factory de Query pré-configurées
		 */
		public QueryFactory(String datasource, Type type, String returnType) {
			baseQuery = new Query();
			baseQuery.setDatasource(datasource);
			baseQuery.setType(type);
			baseQuery.setReturnType(returnType);
		}

		/**
		 * @return QueryBuilder pré-rempli selon la configuration de la Factory
		 */
		public QueryBuilder createBuilder() {
			return new QueryBuilder(baseQuery);
		}
		
		/**
		 * @return Query pré-remplie selon la configuration de la Factory
		 */
		public Query create() {
			return createBuilder().build();
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Fluent Builder pour Query
	 */
	public static class QueryBuilder {
		private final Query query;
		
		public QueryBuilder() {
			query = new Query();
		}

		protected QueryBuilder(Query baseQuery) {
			this.query = new Query(baseQuery);
		}

		public QueryBuilder datasource(String datasource) {
			query.setDatasource(datasource);
			return this;
		}
		
		public QueryBuilder type(Type type) {
			query.setType(type);
			return this;
		}

		public QueryBuilder statement(String statement) {
			query.setStatement(statement);
			return this;
		}

		public QueryBuilder parameters(Object parameters) {
			query.setParameters(parameters);
			return this;
		}

		public QueryBuilder returnType(String returnType) {
			query.setReturnType(returnType);
			return this;
		}

		public QueryBuilder returnTypeClass(Class<?> returnTypeClass) {
			query.setReturnTypeClass(returnTypeClass);
			return this;
		}

		public QueryBuilder otherOptions(Map<String, String> otherOptions) {
			query.setOtherOptions(otherOptions);
			return this;
		}
		
		/**
		 * @return Query
		 * @throws IllegalStateException si l'objet Query est dans un état incomplet
		 */
		public Query build() throws IllegalStateException {
			query.checkStateLegality();
			return query;
		}
	}
}
