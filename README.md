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

## 5. Api document

- OpenAPI JSON: http://localhost:8080/v3/api-docs

- Swagger: http://localhost:8080/swagger-ui/index.html

### Đăng nhập bằng Google (API)

`POST /api/auth/social/google` với body `{"token": "<Google ID token>"}` trả về cặp JWT giống `/api/auth/login`.

1. Google Cloud Console → APIs & Services → Credentials → Create OAuth client ID (loại *Web application*).
2. Đặt `GOOGLE_CLIENT_ID=<client id>` trong `.env`.
3. Client đăng nhập Google bằng chính client id đó (Google Identity Services trên web, Google Sign-In trên mobile) rồi gửi ID token (`credential`) lên API.

Chi tiết request/response và mã lỗi xem trên Swagger, mục **auth**.
