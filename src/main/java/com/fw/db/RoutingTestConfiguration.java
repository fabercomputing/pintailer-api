package com.fw.db;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.fw.pintailer.constants.PintailerConstants;

@Configuration
public class RoutingTestConfiguration {
	
	@Autowired
	private Environment env;

	@Bean
	public DataSource clientDatasource() {
		Map<Object, Object> targetDataSources = new HashMap<>();
		ClientDataSourceRouter clientRoutingDatasource = new ClientDataSourceRouter();
		// boolean firstTimeFlg = true;
		
		String[] orgList = env.getProperty("spring.org.name.list").split(",");
		for (String orgName : orgList) {
			targetDataSources.put(orgName,
					getBasicDataSourceByHostNameProd(orgName));
			clientRoutingDatasource.setTargetDataSources(targetDataSources);
			// if (firstTimeFlg) {
			// clientRoutingDatasource
			// .setDefaultTargetDataSource(targetDataSources
			// .get(ClientDatabase.valueOf(enums.name())));
			// firstTimeFlg = false;
			// }
		}
		return clientRoutingDatasource;
	}

	private DataSource getBasicDataSourceByHostName(String orgName) {
		return DataSourceBuilder
				.create()
				.driverClassName("org.postgresql.Driver")
				.url("jdbc:postgresql://localhost:6000/" + getDBName(orgName)
						+ "?currentSchema=fw_test_mgmt").username("postgres")
				.password("postgres").build();
	}
	
	private DataSource getBasicDataSourceByHostNameProd(String orgName) {
		return DataSourceBuilder
				.create()
				.driverClassName("org.postgresql.Driver")
				.url("jdbc:postgresql://pintailerDbContainer/" + getDBName(orgName)
						+ "?currentSchema=fw_test_mgmt").username("postgres")
				.password("postgres").build();
	}

	private String getDBName(String orgName) {
		StringBuilder db = new StringBuilder();
		db.append(PintailerConstants.DEFAULT_DB_NAME);
		db.append("_");
		db.append(orgName.toLowerCase());
		return db.toString();
	}
}
