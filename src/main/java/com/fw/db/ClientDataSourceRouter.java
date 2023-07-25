package com.fw.db;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ClientDataSourceRouter extends AbstractRoutingDataSource {

//	private static Logger log = Logger.getLogger(ClientDataSourceRouter.class);

	@Override
	protected Object determineCurrentLookupKey() {

//		if (ClientDatabaseContextHolder.getClientDatabase() == null) {
//			log.error("Error : Default user organization is not defined");
//		} else {
//			log.info("*************** User default organization ************************ : "
//					+ ClientDatabaseContextHolder.getClientDatabase());
//		}
		return ClientDatabaseContextHolder.getClientDatabase();
	}
}