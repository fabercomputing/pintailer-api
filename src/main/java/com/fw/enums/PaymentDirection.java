package com.fw.enums;

/**
 * 
 * @author Sumit Srivastava
 *
 */
public enum PaymentDirection {

	CREDIT("CREDIT"), DEBIT("DEBIT");

	private final String dbString;

	private PaymentDirection(String dbString) {
		this.dbString = dbString;
	}

	public String toDbString() {
		return (this.dbString);
	}

	public static PaymentDirection fromString(String str) {
		if ("CREDIT".equalsIgnoreCase(str)) {
			return (CREDIT);
		} else if ("DEBIT".equalsIgnoreCase(str)) {
			return (DEBIT);
		} else {
			throw new RuntimeException("No such criteria exist:" + str);
		}
	}
}
