package com.fw.enums;

/**
 * 
 * @author Sumit Srivastava
 *
 */
public enum TestResults {

	PASSED("PASSED"), FAILED("FAILED"), NOT_TESTED("NOT_TESTED"), IN_PROCESS(
			"IN_PROCESS"), BLOCKED("BLOCKED"), DELETED("DELETED"), SKIPPED(
			"SKIPPED"), PENDING("PENDING");

	private final String dbString;

	private TestResults(String dbString) {
		this.dbString = dbString;
	}

	public String toDbString() {
		return (this.dbString);
	}

	public static TestResults fromString(String str) {
		if ("PASSED".equalsIgnoreCase(str)) {
			return (PASSED);
		} else if ("PASS".equalsIgnoreCase(str)) {
			return (PASSED);
		} else if ("FAILED".equalsIgnoreCase(str)) {
			return (FAILED);
		} else if ("FAIL".equalsIgnoreCase(str)) {
			return (FAILED);
		} else if ("NOT_TESTED".equalsIgnoreCase(str)) {
			return (NOT_TESTED);
		} else if ("IN_PROCESS".equalsIgnoreCase(str)) {
			return (IN_PROCESS);
		} else if ("BLOCKED".equalsIgnoreCase(str)) {
			return (BLOCKED);
		} else if ("BLOCK".equalsIgnoreCase(str)) {
			return (BLOCKED);
		} else if ("DELETED".equalsIgnoreCase(str)) {
			return (DELETED);
		} else if ("SKIP".equalsIgnoreCase(str)) {
			return (SKIPPED);
		} else if ("SKIPPED".equalsIgnoreCase(str)) {
			return (SKIPPED);
		} else if ("PENDING".equalsIgnoreCase(str)) {
			return (PENDING);
		} else {
			throw new RuntimeException("No such TestResults exist:" + str);
		}
	}
}
