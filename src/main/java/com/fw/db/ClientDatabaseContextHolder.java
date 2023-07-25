package com.fw.db;

import org.springframework.util.Assert;

public class ClientDatabaseContextHolder {

//	private static Logger log = Logger
//			.getLogger(ClientDatabaseContextHolder.class);

	private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

	public static void set(String clientOrg) {
		Assert.notNull(clientOrg, "clientDatabase cannot be null");
//		log.info("*************** User default organization is set to ************************ : "
//				+ clientOrg);
		CONTEXT.set(clientOrg);
	}

	public static String getClientDatabase() {
		return CONTEXT.get();
	}

	public static void clear() {
		CONTEXT.remove();
	}
}
