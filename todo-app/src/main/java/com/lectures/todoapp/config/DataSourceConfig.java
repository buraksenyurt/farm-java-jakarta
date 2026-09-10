package com.lectures.todoapp.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.enterprise.context.ApplicationScoped;

@DataSourceDefinition(
        name = "java:app/jdbc/todoDB",
        className = "org.postgresql.ds.PGSimpleDataSource",
        serverName = "localhost",
        portNumber = 5432,
        databaseName = "postgres",
        user = "johndoe",
        password = "somew0rds"
)
@ApplicationScoped
public class DataSourceConfig {

}
