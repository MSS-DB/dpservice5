package com.mbb.bts.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@EnableJpaRepositories(entityManagerFactoryRef = "casaEntityManagerFactory", basePackages = { "com.mbb.bts.casa.repo" })
public class CasaDbConfig {

	@Profile("dev")
	@Primary
	@Bean(name = "casaDataSourceProperties")
	@ConfigurationProperties("casa.datasource")
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}

	@Profile("dev")
	@Primary
	@Bean(name = "casaDataSource")
	@ConfigurationProperties("casa.datasource.configuration")
	public DataSource dataSource(@Qualifier("casaDataSourceProperties") DataSourceProperties dataSourceProperties) {
		return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	@Profile({ "sit", "uat", "prod" })
	@Bean
	@ConfigurationProperties(prefix = "casa.datasource")
	public JndiPropertyHolder primary() {
		return new JndiPropertyHolder();
	}

	@Profile({ "sit", "uat", "prod" })
	@Primary
	@Bean(name = "casaDataSource")
	public DataSource primaryDataSource() {
		JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
		return dataSourceLookup.getDataSource(primary().getJndiName());
	}

	@Primary
	@Bean(name = "casaEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("casaDataSource") DataSource dataSource) {
		return builder.dataSource(dataSource).packages("com.mbb.bts.casa.model").persistenceUnit("casa").build();
	}

	@Primary
	@Bean(name = "casaTransactionManager")
	public PlatformTransactionManager transactionManager(
			@Qualifier("casaEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
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
