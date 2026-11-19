# CEVC_java_phuong

## 1. Requirements

- JDK 21
- Docker + Docker Compose (để chạy PostgreSQL)

## 2. Config

2.1 Copy env

```
cd foodapp/src/main/resources
cp .env.example .env
```

2.2 Change env (khớp với giá trị mặc định trong `foodapp/docker-compose.yml`)

```
DB_URL=jdbc:postgresql://localhost:5432/foodapp_dev_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=<openssl rand -base64 32>
```

## 3. Database (PostgreSQL bằng Docker)

```
cd foodapp

# Start database
docker compose up -d

# Kiểm tra trạng thái (STATUS = healthy là OK)
docker compose ps

# Xem log
docker compose logs -f postgres

# Vào psql
docker exec -it foodapp-postgres psql -U postgres -d foodapp_dev_db

# Stop (giữ data)
docker compose down

# Reset (xóa toàn bộ data)
docker compose down -v
```

Bảng và master data được Flyway tạo tự động khi chạy app (`src/main/resources/db/migration`).

### Mock data

Chạy app ít nhất 1 lần (để Flyway tạo bảng), sau đó:

```
cd foodapp

# Xóa toàn bộ data nghiệp vụ (users, products, orders, ...) và sinh lại data mẫu.
# Master data (roles, product_types, order_statuses, categories, ...) được giữ nguyên.
docker exec -i foodapp-postgres psql -U postgres -d foodapp_dev_db -v ON_ERROR_STOP=1 < scripts/seed-mock-data.sql
```

Tài khoản mẫu (password chung: `Abc12345`):

| Role  | Email                                         |
|-------|-----------------------------------------------|
| Admin | admin01@example.com, admin02@example.com      |
| User  | user01@example.com ... user20@example.com     |

Ảnh product/avatar mẫu là link ngoài (loremflickr, pravatar) nên cần có internet để hiển thị.

## 4. Run application

```
cd foodapp

# Run
./mvnw spring-boot:run

# Build + run file jar
./mvnw clean package -DskipTests
java -jar target/foodapp-*.jar

# Run test
./mvnw test
```

App chạy tại: http://localhost:8080

- Admin: http://localhost:8080/admin/login

### Test

`./mvnw test` cần Docker đang chạy, không cần `.env`, không đụng vào `foodapp_dev_db`:

- Integration test chạy trên một container `postgres:16-alpine` riêng do Testcontainers bật lên, Flyway chạy đủ migration, chạy xong container tự xóa.
- Profile `test`: `src/test/resources/application-test.yaml` (log mức WARN; cần debug thì tạm nâng level ở đây).

```
./mvnw test                                 # toàn bộ test
./mvnw test -DexcludedGroups=integration    # chỉ unit test, không cần Docker
./mvnw test -Dgroups=integration            # chỉ integration test
./mvnw test -Dtest=ProductServiceStockIntegrationTest   # một class
```

Coverage (tắt mặc định vì làm mỗi lần chạy chậm thêm ~4 s): `./mvnw test -Pcoverage`, rồi mở `foodapp/target/site/jacoco/index.html`.

Khoảng 15 s đầu mỗi lần chạy là chi phí khởi động cố định (Maven compile, bật container, Flyway, Spring context), không tăng theo số test. Khi đang viết một test, chạy riêng nó bằng nút Run/Debug trong IDE cho nhanh.

- Quy ước đặt test (chi tiết trong `src/test/java/com/foodorder/foodapp/support/IntegrationTest.java`):
  - `XxxTest`: unit test (JUnit + Mockito, không Spring, không DB).
  - `XxxIntegrationTest`: gắn `@IntegrationTest`, chạy với PostgreSQL thật.
  - Test đặt cùng package với class được test; mỗi test tự tạo và tự xóa dữ liệu của nó.

## 5. Api document

- OpenAPI JSON: http://localhost:8080/v3/api-docs

- Swagger: http://localhost:8080/swagger-ui/index.html
