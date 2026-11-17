package com.foodorder.foodapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.PostgreSqlJdbcIndexedSessionRepositoryCustomizer;

/**
 * Spring Session JDBC tuning for PostgreSQL.
 */
@Configuration
public class SessionConfig {

  /**
   * Session attributes are stored one row per attribute. Two concurrent requests of the same session
   * (e.g. double-clicking "Save") may insert the same attribute; this switches the insert to
   * INSERT ... ON CONFLICT DO UPDATE so the later request overwrites instead of failing with a duplicate key.
   */
  @Bean
  public PostgreSqlJdbcIndexedSessionRepositoryCustomizer sessionRepositoryCustomizer() {
    return new PostgreSqlJdbcIndexedSessionRepositoryCustomizer();
  }
}
