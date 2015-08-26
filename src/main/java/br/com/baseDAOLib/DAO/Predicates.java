package br.com.baseDAOLib.DAO;


public enum Predicates {
	notEqual("!="),
	greaterThanOrEqualTo(">="),
	lessThanOrEqualTo("<="),
	greaterThan(">"),
	lessThan("<"),
	equal("="),
	like("=@=");
	
	private String value;
	
	private Predicates(String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
}
