package com.foodorder.foodapp.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * The PostgreSQL database used by every integration test: a throwaway container with the same
 * image as docker-compose.yml, migrated by Flyway on startup, so tests never touch foodapp_dev_db.
 *
 * <p>One container per test run (JVM): it is static and the bean does not stop it, so test
 * classes that need their own Spring context (e.g. one with a {@code @MockitoSpyBean}) reuse it
 * instead of starting another. Testcontainers removes it when the JVM exits.
 *
 * <p>Container reuse between runs ({@code withReuse}) was measured and left out: it saved ~1.5 s
 * per run but keeps a container running and old rows in the database.
 *
 * <p>Shared file: change it only for something every integration test needs.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

  private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

  /** {@code @ServiceConnection} points spring.datasource (and Flyway) at the container. */
  @Bean(destroyMethod = "")
  @ServiceConnection
  PostgreSQLContainer<?> postgresContainer() {
    return POSTGRES;
  }
}
