package com.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Configuration
public class SpringMongoConfig extends AbstractMongoConfiguration {
	@Value("${spring.data.mongodb.database}")
	private String mongoDB;

	@Value("${spring.data.mongodb.uri}")
	private String mongoUri;

	public @Bean MongoTemplate mongoTemplate() throws Exception {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoClient(), getDatabaseName());
		return mongoTemplate;
	}

	@Override
	public MongoClient mongoClient() {
		return new MongoClient(new MongoClientURI(mongoUri));
	}

	@Override
	protected String getDatabaseName() {
		return mongoDB;
	}

	@Bean
	public MongoOperations mongoOperations() throws Exception {
		return mongoTemplate();
	}

}