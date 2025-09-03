//package com.example.surveyapp.global;
//
//import org.junit.jupiter.api.extension.BeforeAllCallback;
//import org.junit.jupiter.api.extension.ExtensionContext;
//import org.testcontainers.containers.GenericContainer;
//
//public class RedisTestContainersExtension implements BeforeAllCallback {
//    private static final String REDIS_IMAGE = "redis:latest";
//    private static final int REDIS_PORT = 6379;
//    private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
//            .withExposedPorts(REDIS_PORT)
//            .withReuse(true);
//
//    static {
//        REDIS_CONTAINER.start();
//    }
//
//    @Override
//    public void beforeAll(ExtensionContext context) {
//        System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
//        System.setProperty("spring.data.redis.port", String.valueOf(REDIS_CONTAINER.getMappedPort(REDIS_PORT)));
//    }
//}