package com.fw.enums;

/**
 * 
 * @author Sumit Srivastava
 *
 */
public enum TicketType {

	EPIC("EPIC"), CHAPTER("CHAPTER"), STORY("STORY"), IMPROVEMENT("IMPROVEMENT"), BUG("BUG"), TASK("TASK"), SUB_TASK(
			"SUB_TASK");

	private final String dbString;

	private TicketType(String dbString) {
		this.dbString = dbString;
	}

	public String toDbString() {
		return (this.dbString);
	}

	public static TicketType fromString(String str) {
		if ("EPIC".equalsIgnoreCase(str)) {
			return (EPIC);
		} else if ("CHAPTER".equals(str)) {
			return (CHAPTER);
		} else if ("STORY".equals(str)) {
			return (STORY);
		} else if ("IMPROVEMENT".equals(str)) {
			return (IMPROVEMENT);
		} else if ("BUG".equals(str)) {
			return (BUG);
		} else if ("TASK".equals(str)) {
			return (TASK);
		} else if ("SUB_TASK".equals(str)) {
			return (SUB_TASK);
		} else {
			throw new RuntimeException("No such Levels type:" + str);
		}
	}

}
