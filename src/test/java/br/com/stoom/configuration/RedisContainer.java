package br.com.stoom.configuration;

import org.testcontainers.containers.GenericContainer;

public class RedisContainer extends GenericContainer<RedisContainer> {

    private static RedisContainer container;

    private RedisContainer() {
        super("redis:latest");
    }

    public static RedisContainer getInstance() {
        if (container == null) {
            container = new RedisContainer().withExposedPorts(6379);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("REDIS_HOST", container.getHost());
        System.setProperty("REDIS_PORT", String.valueOf(container.getFirstMappedPort()));
    }

    @Override
    public void stop() {

    }
}
