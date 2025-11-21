# CEVC_java_phuong

1. Config

1.1 Copy env

```
cd foodapp/src/main/resources
cp .env.example .env
```

1.2 Change env

1.3 Install postgresql

1.4 Create database

```
psql -U postgres -h localhost
CREATE DATABASE foodapp_dev_db;
```

2. Api document

- OpenAPI JSON: http://localhost:8080/v3/api-docs

- Swagger: http://localhost:8080/swagger-ui/index.html
