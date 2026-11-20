package com.foodorder.foodapp.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Marks a test that runs the full Spring context against the test PostgreSQL container
 * ({@link TestcontainersConfiguration}) with the "test" profile (src/test/resources/application-test.yaml).
 *
 * <h2>Test layout (keep it so parallel branches add files instead of editing the same ones)</h2>
 * <ul>
 *   <li>Test classes mirror the main package: {@code service/ProductService} is tested in
 *       {@code service/ProductService*Test}.</li>
 *   <li>{@code XxxTest}: unit test, plain JUnit + Mockito, no Spring and no database. For logic
 *       that does not depend on the database.</li>
 *   <li>{@code XxxIntegrationTest}: annotated with {@code @IntegrationTest}. For transactions,
 *       locking, queries, migrations: anything only a real PostgreSQL can prove.</li>
 *   <li>One class per production class and kind; split by feature
 *       (e.g. {@code ProductServiceStockIntegrationTest}) when a class grows, instead of
 *       appending unrelated tests to one file.</li>
 *   <li>Each test creates the rows it needs and deletes them afterwards (the container is shared
 *       by the whole run). Do not depend on scripts/seed-mock-data.sql; master data inserted by
 *       Flyway migrations (categories, product types, order statuses) may be read.</li>
 *   <li>Test data helpers stay private to the test class that uses them. Move one to
 *       {@code support/} only once several test classes need it.</li>
 *   <li>{@code support/} and application-test.yaml are shared: change them only for something
 *       every test needs.</li>
 *   <li>Import {@code org.junit.jupiter.api.Test}, never {@code org.junit.Test}: JUnit 4 is on the
 *       classpath only because Testcontainers 1.x needs it, and a JUnit 4 test is silently skipped
 *       (no vintage engine).</li>
 * </ul>
 *
 * <p>Tagged {@value #TAG}: {@code ./mvnw test -DexcludedGroups=integration} runs only the unit
 * tests (no Docker needed), {@code ./mvnw test -Dgroups=integration} only the integration tests.
 *
 * <p>Classes with the same annotations share one cached Spring context. Adding
 * {@code @MockitoSpyBean}/{@code @MockitoBean} creates a separate context (slower), so use it only
 * when a test must intercept a bean.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag(IntegrationTest.TAG)
@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public @interface IntegrationTest {
  /** JUnit tag of every integration test, used by -Dgroups / -DexcludedGroups. */
  String TAG = "integration";
}
