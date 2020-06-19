package br.com.stoom.configuration;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresDatabaseContainer extends PostgreSQLContainer<PostgresDatabaseContainer> {

    private static PostgresDatabaseContainer container;

    private PostgresDatabaseContainer() {
        super("postgres:latest");
    }

    public static PostgresDatabaseContainer getInstance() {
        if (container == null) {
            container = new PostgresDatabaseContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USER", container.getUsername());
        System.setProperty("DB_PASS", container.getPassword());
    }

    @Override
    public void stop() {

    }
}
