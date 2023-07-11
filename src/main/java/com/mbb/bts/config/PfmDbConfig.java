package com.mbb.bts.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "pfmEntityManagerFactory", basePackages = { "com.mbb.bts.pfm.repo" })
public class PfmDbConfig {

	@Profile("dev")
	@Bean(name = "pfmDataSourceProperties")
	@ConfigurationProperties("pfm.datasource")
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}

	@Profile("dev")
	@Bean(name = "pfmDataSource")
	@ConfigurationProperties("pfm.datasource.configuration")
	public DataSource dataSource(@Qualifier("pfmDataSourceProperties") DataSourceProperties dataSourceProperties) {
		return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	@Profile({ "sit", "uat", "prod" })
	@Bean
	@ConfigurationProperties(prefix = "pfm.datasource")
	public JndiPropertyHolder secondary() {
		return new JndiPropertyHolder();
	}

	@Profile({ "sit", "uat", "prod" })
	@Bean(name = "pfmDataSource")
	public DataSource secondaryDataSource() {
		JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
		return dataSourceLookup.getDataSource(secondary().getJndiName());
	}

	@Bean(name = "pfmEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("pfmDataSource") DataSource dataSource) {
		return builder.dataSource(dataSource).packages("com.mbb.bts.pfm.model").persistenceUnit("pfm").build();
	}

	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager(
			@Qualifier("pfmEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	private static class JndiPropertyHolder {
		private String jndiName;

		public String getJndiName() {
			return jndiName;
		}

		@SuppressWarnings("unused")
		public void setJndiName(String jndiName) {
			this.jndiName = jndiName;
		}
	}

}