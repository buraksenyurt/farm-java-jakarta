package com.lectures.rest;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@DataSourceDefinition(
        name = "java:app/jdbc/todoDB",
        className = "org.postgresql.ds.PGSimpleDataSource",
        serverName = "localhost",
        portNumber = 5432,
        databaseName = "postgres",
        user = "johndoe",
        password = "somew0rds"
)
@ApplicationPath("api/v1")
public class TodoConfiguration extends Application {

}
