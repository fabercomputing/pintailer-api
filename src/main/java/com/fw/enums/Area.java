package com.fw.enums;

/**
 * 
 * @author Sumit Srivastava
 *
 */

public enum Area {

	UI("UI"), API("API");

	private final String dbString;

	private Area(String dbString) {
		this.dbString = dbString;
	}

	public String toDbString() {
		return (this.dbString);
	}

	public static Area fromString(String str) {
		if ("UI".equalsIgnoreCase(str)) {
			return (UI);
		} else if ("API".equals(str)) {
			return (API);
		} else {
			throw new RuntimeException("No such Area type:" + str);
		}
	}

}
