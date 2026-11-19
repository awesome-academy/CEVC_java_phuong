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

2.3 Thông báo đơn hàng (tuỳ chọn, để trống thì bỏ qua kênh đó)

```
# Tin nhắn Chatwork mỗi khi có đơn mới
CHATWORK_API_TOKEN=<API token>
CHATWORK_ROOM_ID=<id trong URL của room, #!rid...>

# Email cho tất cả user ADMIN: khi có đơn mới, và báo cáo tháng trước lúc 08:00 ngày 1.
# Local: Mailpit trong docker-compose bắt mọi email, xem tại http://localhost:8025
MAIL_ENABLED=true
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

## 5. Api document

- OpenAPI JSON: http://localhost:8080/v3/api-docs

- Swagger: http://localhost:8080/swagger-ui/index.html
