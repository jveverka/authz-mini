package one.microproject.authx.service.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(initializers = AppBaseTest.Initializer.class)
public abstract class AppBaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(AppBaseTest.class);

    private static final int DOCKER_EXPOSED_MONGO_PORT = 27017;
    private static final int DOCKER_EXPOSED_REDIS_PORT = 6379;

    private static final String MONGO_DOCKER_IMAGE = "mongo:4.4.10-focal";
    private static final String REDIS_DOCKER_IMAGE = "redis:6-alpine";

    private static MongoDBContainer mongoDBContainer;
    private static GenericContainer<?> redisContainer;

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        private final Logger LOGGER = LoggerFactory.getLogger(AppBaseTest.class);

        private static MongoDBContainer mongoDBContainer = new MongoDBContainer(MONGO_DOCKER_IMAGE);
        private static GenericContainer<?> redisContainer = new GenericContainer<>(REDIS_DOCKER_IMAGE)
                .withExposedPorts(DOCKER_EXPOSED_REDIS_PORT)
                .withReuse(true);

        @Override
        public void initialize(@NonNull ConfigurableApplicationContext context) {

            redisContainer.start();
            Assertions.assertTrue(redisContainer.isRunning());
            AppBaseTest.redisContainer = redisContainer;
            Integer redisBoundPort = redisContainer.getMappedPort(DOCKER_EXPOSED_REDIS_PORT);

            mongoDBContainer.start();
            Assertions.assertTrue(mongoDBContainer.isRunning());
            AppBaseTest.mongoDBContainer = mongoDBContainer;
            Integer mongoBoundPort = mongoDBContainer.getMappedPort(DOCKER_EXPOSED_MONGO_PORT);

            LOGGER.info("MONGO      : {}", mongoDBContainer.getReplicaSetUrl());
            LOGGER.info("MONGO      : mongodb://localhost:{}/test", mongoBoundPort);
            LOGGER.info("REDIS      : {}:{}", redisContainer.getContainerIpAddress(), redisBoundPort);

            TestPropertyValues.of(
                    "spring.data.mongodb.host=localhost",
                    "spring.data.mongodb.port=" + mongoBoundPort,
                    "spring.data.mongodb.database=test",
                    "spring.redis.host=" + redisContainer.getContainerIpAddress(),
                    "spring.redis.host=" + redisBoundPort
            ).applyTo(context.getEnvironment());
        }
    }

}
