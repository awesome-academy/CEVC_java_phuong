-- =====================================================================
-- seed-mock-data.sql
-- Mock data for local development / demo.
--
-- !!! Script XÓA SẠCH dữ liệu nghiệp vụ (users, products, orders, ...) rồi sinh lại.
-- Master data (roles, auth_providers, product_types, order_statuses, categories)
-- do Flyway quản lý nên được giữ nguyên.
--
-- Run (từ thư mục foodapp, app phải đã chạy ít nhất 1 lần để Flyway tạo bảng):
--   docker exec -i foodapp-postgres psql -U postgres -d foodapp_dev_db -v ON_ERROR_STOP=1 < scripts/seed-mock-data.sql
--
-- Tài khoản (password chung: Abc12345)
--   Admin: admin01@example.com, admin02@example.com
--   User : user01@example.com ... user20@example.com
--
-- Kết quả: 2 admin, 20 user, 25 địa chỉ, 40 products, 70 orders,
--          ~53 evaluates, 12 carts, 15 suggestions.
-- Dữ liệu random có setseed nên mỗi lần chạy ra cùng một bộ dữ liệu.
-- =====================================================================

-- Container Postgres chạy UTC, app ghi LocalDateTime theo giờ VN -> NOW() phải theo giờ VN
SET TIME ZONE 'Asia/Bangkok';

BEGIN;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ---------------------------------------------------------------------
-- 0. Clear business data (keep master data)
-- ---------------------------------------------------------------------
TRUNCATE
  order_addresses, order_items, orders,
  cart_items, carts,
  evaluates, suggestions,
  user_addresses, products, users
RESTART IDENTITY CASCADE;

-- ---------------------------------------------------------------------
-- 1. Users: 2 admin + 20 user (LOCAL login)
-- ---------------------------------------------------------------------
WITH pw AS (SELECT crypt('Abc12345', gen_salt('bf', 10)) AS hash)
INSERT INTO users (role_id, auth_provider_id, full_name, email, password, age, avatar, created_at, updated_at)
SELECT r.id, ap.id, v.full_name, v.email, pw.hash, v.age,
       'https://i.pravatar.cc/300?img=' || v.avatar_no,
       NOW() - (v.days_ago || ' days')::INTERVAL,
       NOW() - (v.days_ago || ' days')::INTERVAL
FROM (
  VALUES
    ('ADMIN', 'Phạm Quản Trị',     'admin01@example.com', 30, 12, 200),
    ('ADMIN', 'Lê Vận Hành',       'admin02@example.com', 28, 59, 190),
    ('USER',  'Nguyễn Văn An',     'user01@example.com',  24, 11, 180),
    ('USER',  'Trần Thị Bình',     'user02@example.com',  22, 5,  178),
    ('USER',  'Lê Hoàng Cường',    'user03@example.com',  27, 13, 175),
    ('USER',  'Phạm Thu Dung',     'user04@example.com',  25, 9,  170),
    ('USER',  'Hoàng Minh Đức',    'user05@example.com',  31, 14, 168),
    ('USER',  'Vũ Thị Giang',      'user06@example.com',  20, 16, 165),
    ('USER',  'Đặng Quốc Huy',     'user07@example.com',  29, 15, 160),
    ('USER',  'Bùi Thanh Hương',   'user08@example.com',  23, 20, 158),
    ('USER',  'Đỗ Văn Khánh',      'user09@example.com',  35, 33, 155),
    ('USER',  'Ngô Thị Lan',       'user10@example.com',  26, 23, 150),
    ('USER',  'Dương Văn Long',    'user11@example.com',  33, 51, 148),
    ('USER',  'Lý Thị Mai',        'user12@example.com',  21, 25, 145),
    ('USER',  'Phan Văn Nam',      'user13@example.com',  28, 52, 140),
    ('USER',  'Hồ Thị Oanh',       'user14@example.com',  24, 26, 135),
    ('USER',  'Trịnh Văn Phúc',    'user15@example.com',  30, 53, 130),
    ('USER',  'Mai Thị Quỳnh',     'user16@example.com',  19, 29, 128),
    ('USER',  'Cao Văn Sơn',       'user17@example.com',  40, 54, 125),
    ('USER',  'Lâm Thị Thảo',      'user18@example.com',  22, 32, 122),
    ('USER',  'Tạ Minh Tuấn',      'user19@example.com',  27, 56, 120),
    ('USER',  'Kiều Thị Vân',      'user20@example.com',  25, 45, 118)
) AS v (role_name, full_name, email, age, avatar_no, days_ago)
JOIN roles r ON r.name = v.role_name
JOIN auth_providers ap ON ap.name = 'LOCAL'
CROSS JOIN pw
ORDER BY v.days_ago DESC;   -- giữ id theo thứ tự khai báo: admin01=1, admin02=2, user01=3 ...

-- ---------------------------------------------------------------------
-- 2. User addresses: mỗi user 1 địa chỉ nhà, 5 user đầu thêm địa chỉ công ty
-- ---------------------------------------------------------------------
INSERT INTO user_addresses (user_id, receiver_name, phone_number, city, district, street_address, created_at, updated_at)
SELECT u.id, u.full_name,
       '09' || LPAD((12345678 + u.id * 7919)::TEXT, 8, '0'),
       a.city, a.district, a.street,
       u.created_at, u.created_at
FROM users u
JOIN roles r ON r.id = u.role_id AND r.name = 'USER'
JOIN (
  VALUES
    (0, 'Hà Nội',      'Cầu Giấy',     '12 Trần Thái Tông'),
    (1, 'Hà Nội',      'Đống Đa',      '45 Tây Sơn'),
    (2, 'Hà Nội',      'Hai Bà Trưng', '88 Bạch Mai'),
    (3, 'Hà Nội',      'Thanh Xuân',   '210 Nguyễn Trãi'),
    (4, 'Hà Nội',      'Nam Từ Liêm',  '7 Hàm Nghi'),
    (5, 'Hà Nội',      'Hoàn Kiếm',    '23 Hàng Bài'),
    (6, 'Hồ Chí Minh', 'Quận 1',       '150 Nguyễn Thị Minh Khai'),
    (7, 'Hồ Chí Minh', 'Quận 3',       '72 Võ Văn Tần'),
    (8, 'Hồ Chí Minh', 'Bình Thạnh',   '19 Điện Biên Phủ'),
    (9, 'Đà Nẵng',     'Hải Châu',     '35 Bạch Đằng')
) AS a (idx, city, district, street) ON a.idx = u.id % 10;

INSERT INTO user_addresses (user_id, receiver_name, phone_number, city, district, street_address, created_at, updated_at)
SELECT u.id, u.full_name || ' (Công ty)', '0243' || LPAD((1000000 + u.id * 31)::TEXT, 7, '0'),
       'Hà Nội', 'Nam Từ Liêm', 'Tầng 13, Keangnam Landmark 72, Phạm Hùng',
       u.created_at + INTERVAL '10 days', u.created_at + INTERVAL '10 days'
FROM users u
WHERE u.email IN ('user01@example.com', 'user02@example.com', 'user03@example.com', 'user04@example.com', 'user05@example.com');

-- ---------------------------------------------------------------------
-- 3. Products: 40 sản phẩm (25 FOOD, 15 DRINK)
--    - "Bánh mì chảo", "Nước ép dưa hấu": is_active = false (ẩn phía user)
--    - "Chân gà sả tắc": quantity = 0 (hết hàng)
-- ---------------------------------------------------------------------
INSERT INTO products (category_id, product_type_id, name, description, price, quantity, image, average_rating, is_active, created_at, updated_at)
SELECT c.id, pt.id, v.name, v.description, v.price, v.quantity,
       'https://loremflickr.com/480/360/' || v.image_keyword || '?lock=' || ROW_NUMBER() OVER (),
       0, v.is_active,
       NOW() - INTERVAL '150 days', NOW() - INTERVAL '150 days'
FROM (
  VALUES
    -- Cơm
    ('Cơm',               'FOOD',  'Cơm tấm sườn bì chả',          'Sườn nướng than hoa, bì, chả trứng, mỡ hành và nước mắm chua ngọt.',            45000, 100, 'rice,pork',       TRUE),
    ('Cơm',               'FOOD',  'Cơm gà Hải Nam',               'Gà luộc da giòn, cơm nấu nước dùng gà, kèm nước chấm gừng.',                      50000, 80,  'chicken,rice',    TRUE),
    ('Cơm',               'FOOD',  'Cơm rang dưa bò',              'Cơm rang tơi, thịt bò mềm xào dưa cải chua.',                                     45000, 80,  'fried,rice',      TRUE),
    ('Cơm',               'FOOD',  'Cơm chiên hải sản',            'Cơm chiên với tôm, mực, trứng và rau củ.',                                        55000, 60,  'seafood,rice',    TRUE),
    ('Cơm',               'FOOD',  'Cơm sườn xào chua ngọt',       'Sườn non xào sốt chua ngọt, ăn kèm canh và dưa chua.',                            48000, 70,  'pork,rice',       TRUE),
    -- Bún & Phở
    ('Bún & Phở',         'FOOD',  'Phở bò tái chín',              'Nước dùng hầm xương 12 tiếng, bò tái và bò chín, bánh phở mềm.',                  50000, 120, 'pho',             TRUE),
    ('Bún & Phở',         'FOOD',  'Phở gà',                       'Phở gà ta xé, nước dùng thanh ngọt, hành lá và lá chanh.',                        45000, 100, 'pho,chicken',     TRUE),
    ('Bún & Phở',         'FOOD',  'Bún chả Hà Nội',               'Chả viên và chả miếng nướng than, nước chấm đu đủ, bún và rau sống.',             45000, 100, 'noodles,pork',    TRUE),
    ('Bún & Phở',         'FOOD',  'Bún bò Huế',                   'Bún sợi to, giò heo, chả cua, nước dùng sả cay đặc trưng.',                       50000, 90,  'noodles,soup',    TRUE),
    ('Bún & Phở',         'FOOD',  'Bún riêu cua',                 'Riêu cua đồng, đậu phụ chiên, cà chua, ăn kèm rau sống.',                         40000, 90,  'noodles,crab',    TRUE),
    ('Bún & Phở',         'FOOD',  'Mì Quảng',                     'Mì Quảng tôm thịt, đậu phộng rang và bánh tráng mè.',                             45000, 70,  'noodles',         TRUE),
    -- Bánh mì
    ('Bánh mì',           'FOOD',  'Bánh mì thịt nướng',           'Bánh mì giòn, thịt nướng sả, đồ chua, rau thơm và tương ớt.',                    25000, 150, 'banhmi',          TRUE),
    ('Bánh mì',           'FOOD',  'Bánh mì pate trứng',           'Pate gan nhà làm, trứng ốp la, chả lụa và dưa leo.',                              20000, 150, 'sandwich,egg',    TRUE),
    ('Bánh mì',           'FOOD',  'Bánh mì chảo',                 'Chảo gang nóng với trứng, pate, xúc xích, xíu mại, ăn kèm bánh mì.',              40000, 50,  'breakfast,pan',   FALSE),
    -- Đồ ăn nhanh
    ('Đồ ăn nhanh',       'FOOD',  'Gà rán giòn (2 miếng)',        'Gà tẩm bột chiên giòn rụm, kèm tương cà và tương ớt.',                            65000, 80,  'friedchicken',    TRUE),
    ('Đồ ăn nhanh',       'FOOD',  'Burger bò phô mai',            'Bò Úc nướng, phô mai cheddar, xà lách, cà chua, sốt đặc biệt.',                   59000, 60,  'burger',          TRUE),
    ('Đồ ăn nhanh',       'FOOD',  'Khoai tây chiên',              'Khoai tây chiên giòn, rắc muối, kèm sốt mayonnaise.',                             30000, 200, 'frenchfries',     TRUE),
    ('Đồ ăn nhanh',       'FOOD',  'Pizza hải sản (size M)',       'Đế mỏng, tôm, mực, thanh cua, phô mai mozzarella.',                               129000, 40, 'pizza',           TRUE),
    -- Ăn vặt
    ('Ăn vặt',            'FOOD',  'Nem chua rán',                 'Nem chua rán vàng giòn, chấm tương ớt.',                                          35000, 100, 'snack,fried',     TRUE),
    ('Ăn vặt',            'FOOD',  'Bánh tráng trộn',              'Bánh tráng, xoài xanh, trứng cút, khô bò, rau răm, sốt me.',                      25000, 100, 'streetfood',      TRUE),
    ('Ăn vặt',            'FOOD',  'Xoài lắc muối tôm',            'Xoài xanh giòn chua lắc cùng muối tôm Tây Ninh.',                                 30000, 80,  'mango',           TRUE),
    ('Ăn vặt',            'FOOD',  'Chân gà sả tắc',               'Chân gà rút xương ngâm sả tắc chua cay, giòn sần sật.',                           55000, 0,   'chicken,spicy',   TRUE),
    -- Tráng miệng
    ('Tráng miệng',       'FOOD',  'Chè khúc bạch',                'Khúc bạch phô mai, nhãn nhục, hạnh nhân lát, nước đường phèn.',                   30000, 80,  'dessert',         TRUE),
    ('Tráng miệng',       'FOOD',  'Bánh flan caramel',            'Bánh flan mềm mịn, caramel đắng nhẹ, thêm cà phê tùy chọn.',                      15000, 120, 'flan,caramel',    TRUE),
    ('Tráng miệng',       'FOOD',  'Sữa chua nếp cẩm',             'Sữa chua nhà làm, nếp cẩm dẻo thơm.',                                             25000, 100, 'yogurt',          TRUE),
    -- Cà phê
    ('Cà phê',            'DRINK', 'Cà phê đen đá',                'Cà phê phin Robusta đậm đà, uống kèm đá.',                                        20000, 300, 'coffee,black',    TRUE),
    ('Cà phê',            'DRINK', 'Cà phê sữa đá',                'Cà phê phin hòa sữa đặc, vị truyền thống.',                                       25000, 300, 'icedcoffee',      TRUE),
    ('Cà phê',            'DRINK', 'Bạc xỉu',                      'Nhiều sữa ít cà phê, ngọt béo dễ uống.',                                          29000, 200, 'latte',           TRUE),
    ('Cà phê',            'DRINK', 'Cà phê muối',                  'Cà phê phủ lớp kem muối mằn mặn, béo ngậy.',                                      35000, 150, 'coffee,cream',    TRUE),
    -- Trà & Trà sữa
    ('Trà & Trà sữa',     'DRINK', 'Trà sữa trân châu đường đen',  'Trà sữa đậm vị, trân châu đường đen nấu mới mỗi ngày.',                          40000, 200, 'bubbletea',       TRUE),
    ('Trà & Trà sữa',     'DRINK', 'Trà đào cam sả',               'Trà đen, đào miếng, cam tươi và sả thơm mát.',                                    35000, 200, 'peach,tea',       TRUE),
    ('Trà & Trà sữa',     'DRINK', 'Trà chanh',                    'Trà xanh pha chanh tươi, giải nhiệt mùa hè.',                                     15000, 300, 'lemon,tea',       TRUE),
    ('Trà & Trà sữa',     'DRINK', 'Matcha latte',                 'Matcha Nhật Bản đánh cùng sữa tươi.',                                             45000, 120, 'matcha',          TRUE),
    -- Nước ép & Sinh tố
    ('Nước ép & Sinh tố', 'DRINK', 'Nước ép cam',                  'Cam sành vắt tươi 100%, không thêm đường.',                                       30000, 150, 'orangejuice',     TRUE),
    ('Nước ép & Sinh tố', 'DRINK', 'Sinh tố bơ',                   'Bơ sáp Đắk Lắk xay cùng sữa đặc, béo mịn.',                                       40000, 100, 'avocado,smoothie', TRUE),
    ('Nước ép & Sinh tố', 'DRINK', 'Nước ép dưa hấu',              'Dưa hấu ép tươi mát lạnh.',                                                       30000, 0,   'watermelon,juice', FALSE),
    ('Nước ép & Sinh tố', 'DRINK', 'Sinh tố xoài',                 'Xoài cát chín xay cùng sữa chua.',                                                35000, 100, 'mango,smoothie',  TRUE),
    -- Nước giải khát
    ('Nước giải khát',    'DRINK', 'Coca-Cola lon 330ml',          'Nước ngọt có ga, uống lạnh ngon hơn.',                                            15000, 500, 'cola',            TRUE),
    ('Nước giải khát',    'DRINK', 'Nước suối 500ml',              'Nước khoáng thiên nhiên.',                                                        10000, 500, 'water,bottle',    TRUE),
    ('Nước giải khát',    'DRINK', 'Trà xanh đóng chai 455ml',     'Trà xanh vị chanh, đóng chai tiện lợi.',                                          15000, 400, 'greentea,bottle', TRUE)
) AS v (category_name, type_name, name, description, price, quantity, image_keyword, is_active)
JOIN categories c ON c.name = v.category_name AND c.parent_id IS NOT NULL
JOIN product_types pt ON pt.name = v.type_name;

-- Sanity check: đủ 40 product (sai tên category ở V5 thì JOIN rơi mất dòng)
DO $$
BEGIN
  IF (SELECT COUNT(*) FROM products) <> 40 THEN
    RAISE EXCEPTION 'Expected 40 products, got %. Check categories from V5 migration.', (SELECT COUNT(*) FROM products);
  END IF;
END $$;

-- ---------------------------------------------------------------------
-- 4. Orders: 70 đơn trong ~4 tháng gần nhất
--    - 12 đơn gần nhất (0-3 ngày): PENDING / CONFIRMED / SHIPPED / COMPLETED
--    - 58 đơn cũ hơn: ~85% COMPLETED, ~15% CANCELLED (có reason_cancel)
-- ---------------------------------------------------------------------
DO $$
DECLARE
  v_user_id       BIGINT;
  v_order_id      BIGINT;
  v_status        TEXT;
  v_created       TIMESTAMP;
  v_updated       TIMESTAMP;
  v_reason        TEXT;
  v_note          TEXT;
  v_total         INTEGER;
  v_qty           INTEGER;
  v_rand          FLOAT;
  v_prod          RECORD;
  v_reasons TEXT[] := ARRAY[
    'Đặt nhầm món, muốn đặt lại đơn khác',
    'Thời gian giao hàng quá lâu',
    'Thay đổi địa chỉ nhận hàng',
    'Quán hết món trong đơn',
    'Khách không liên lạc được'
  ];
  v_notes TEXT[] := ARRAY[
    'Ít cay giúp mình',
    'Không hành',
    'Giao giờ hành chính',
    'Gọi trước khi giao 5 phút',
    'Thêm đũa thìa',
    'Ít đá'
  ];
BEGIN
  PERFORM setseed(0.42);

  FOR i IN 1..70 LOOP
    SELECT u.id INTO v_user_id
    FROM users u JOIN roles r ON r.id = u.role_id AND r.name = 'USER'
    ORDER BY random() LIMIT 1;

    v_rand   := random();
    v_reason := NULL;

    IF i <= 12 THEN
      v_created := NOW() - (random() * INTERVAL '3 days');
      v_status  := CASE
                     WHEN v_rand < 0.35 THEN 'PENDING'
                     WHEN v_rand < 0.60 THEN 'CONFIRMED'
                     WHEN v_rand < 0.85 THEN 'SHIPPED'
                     ELSE 'COMPLETED'
                   END;
    ELSE
      v_created := NOW() - INTERVAL '4 days' - (random() * INTERVAL '116 days');
      IF v_rand < 0.85 THEN
        v_status := 'COMPLETED';
      ELSE
        v_status := 'CANCELLED';
        v_reason := v_reasons[1 + floor(random() * array_length(v_reasons, 1))::INT];
      END IF;
    END IF;

    v_updated := CASE WHEN v_status = 'PENDING' THEN v_created
                      ELSE v_created + (30 + random() * 90) * INTERVAL '1 minute' END;
    v_note    := CASE WHEN random() < 0.3
                      THEN v_notes[1 + floor(random() * array_length(v_notes, 1))::INT] END;

    INSERT INTO orders (user_id, order_status_id, reason_cancel, total_price, note, created_at, updated_at)
    SELECT v_user_id, os.id, v_reason, 0, v_note, v_created, v_updated
    FROM order_statuses os WHERE os.name = v_status
    RETURNING id INTO v_order_id;

    -- 1-4 sản phẩm khác nhau / đơn, giá chốt tại thời điểm đặt
    v_total := 0;
    FOR v_prod IN
      SELECT id, price FROM products
      WHERE is_active AND quantity > 0
      ORDER BY random()
      LIMIT 1 + floor(random() * 4)::INT
    LOOP
      v_qty   := 1 + floor(random() * 3)::INT;
      v_total := v_total + v_prod.price * v_qty;
      INSERT INTO order_items (order_id, product_id, quantity, price, created_at, updated_at)
      VALUES (v_order_id, v_prod.id, v_qty, v_prod.price, v_created, v_created);
    END LOOP;

    UPDATE orders SET total_price = v_total WHERE id = v_order_id;

    -- Snapshot địa chỉ giao hàng từ địa chỉ đầu tiên của user
    INSERT INTO order_addresses (order_id, receiver_name, phone_number, city, district, street_address, created_at, updated_at)
    SELECT v_order_id, ua.receiver_name, ua.phone_number, ua.city, ua.district, ua.street_address, v_created, v_created
    FROM user_addresses ua WHERE ua.user_id = v_user_id
    ORDER BY ua.id LIMIT 1;
  END LOOP;
END $$;

-- ---------------------------------------------------------------------
-- 5. Evaluates: chỉ user đã có đơn COMPLETED chứa product mới được đánh giá
--    (khớp rule của API), mỗi user đánh giá 1 product tối đa 1 lần.
--    Tối đa 4 đánh giá 1-2 sao bị admin ẩn (is_show = false).
-- ---------------------------------------------------------------------
DO $$
DECLARE
  v_pair    RECORD;
  v_rating  INTEGER;
  v_rand    FLOAT;
  v_content TEXT;
  v_good TEXT[] := ARRAY[
    'Món ngon, giao nhanh, đóng gói cẩn thận. Sẽ ủng hộ tiếp!',
    'Vị đậm đà, phần ăn đầy đặn, đáng tiền.',
    'Rất ngon, giống hệt ăn tại quán.',
    'Shipper thân thiện, đồ ăn còn nóng hổi.',
    'Lần thứ 3 đặt rồi, chất lượng vẫn ổn định.'
  ];
  v_ok TEXT[] := ARRAY[
    'Ăn được, nhưng hơi ít so với giá.',
    'Vị ổn, giao hơi lâu một chút.',
    'Bình thường, không có gì đặc biệt.'
  ];
  v_bad TEXT[] := ARRAY[
    'Đồ ăn nguội, giao trễ gần 1 tiếng.',
    'Thiếu món so với đơn, liên hệ mãi không được.',
    'Quá mặn, không ăn nổi.'
  ];
BEGIN
  PERFORM setseed(0.43);

  FOR v_pair IN
    SELECT DISTINCT ON (o.user_id, oi.product_id) o.user_id, oi.product_id, o.updated_at
    FROM orders o
    JOIN order_statuses os ON os.id = o.order_status_id AND os.name = 'COMPLETED'
    JOIN order_items oi ON oi.order_id = o.id
    ORDER BY o.user_id, oi.product_id, o.updated_at
  LOOP
    CONTINUE WHEN random() > 0.5;   -- ~50% khách để lại đánh giá

    v_rand := random();
    v_rating := CASE
                  WHEN v_rand < 0.45 THEN 5
                  WHEN v_rand < 0.75 THEN 4
                  WHEN v_rand < 0.88 THEN 3
                  WHEN v_rand < 0.95 THEN 2
                  ELSE 1
                END;
    v_content := CASE
                   WHEN v_rating >= 4 THEN v_good[1 + floor(random() * array_length(v_good, 1))::INT]
                   WHEN v_rating = 3  THEN v_ok[1 + floor(random() * array_length(v_ok, 1))::INT]
                   ELSE v_bad[1 + floor(random() * array_length(v_bad, 1))::INT]
                 END;

    INSERT INTO evaluates (user_id, product_id, content, rating, is_show, created_at, updated_at)
    VALUES (v_pair.user_id, v_pair.product_id, v_content, v_rating, TRUE,
            v_pair.updated_at + INTERVAL '1 day', v_pair.updated_at + INTERVAL '1 day');
  END LOOP;
END $$;

UPDATE evaluates SET is_show = FALSE
WHERE id IN (SELECT id FROM evaluates WHERE rating <= 2 ORDER BY rating, id LIMIT 4);

-- average_rating chỉ tính các đánh giá đang hiển thị (giống ClientEvaluateService)
UPDATE products p
SET average_rating = COALESCE((
  SELECT ROUND(AVG(e.rating)::NUMERIC, 1)
  FROM evaluates e
  WHERE e.product_id = p.id AND e.is_show
), 0);

-- ---------------------------------------------------------------------
-- 6. Carts: 12 user đang có giỏ hàng, mỗi giỏ 1-4 sản phẩm
-- ---------------------------------------------------------------------
DO $$
DECLARE
  v_user   RECORD;
  v_cart_id BIGINT;
  v_created TIMESTAMP;
BEGIN
  PERFORM setseed(0.44);

  FOR v_user IN
    SELECT u.id FROM users u JOIN roles r ON r.id = u.role_id AND r.name = 'USER'
    ORDER BY random() LIMIT 12
  LOOP
    v_created := NOW() - (random() * INTERVAL '5 days');

    INSERT INTO carts (user_id, created_at, updated_at)
    VALUES (v_user.id, v_created, v_created)
    RETURNING id INTO v_cart_id;

    INSERT INTO cart_items (cart_id, product_id, quantity, created_at, updated_at)
    SELECT v_cart_id, p.id, 1 + floor(random() * 3)::INT, v_created, v_created
    FROM (
      SELECT id FROM products WHERE is_active AND quantity > 0
      ORDER BY random() LIMIT 1 + floor(random() * 4)::INT
    ) p;
  END LOOP;
END $$;

-- ---------------------------------------------------------------------
-- 7. Suggestions: 15 đề xuất món mới, đủ 4 trạng thái
-- ---------------------------------------------------------------------
INSERT INTO suggestions (user_id, title, description, status, created_at, updated_at)
SELECT u.id, v.title, v.description, v.status,
       NOW() - (v.days_ago || ' days')::INTERVAL,
       NOW() - (v.days_ago || ' days')::INTERVAL + CASE WHEN v.status = 'PENDING' THEN INTERVAL '0' ELSE INTERVAL '1 day' END
FROM (
  VALUES
    ('user01@example.com', 'Thêm món Bún đậu mắm tôm',     'Quán nên có bún đậu mắm tôm, có thể thêm chả cốm và lòng.',              'APPROVED', 60),
    ('user02@example.com', 'Thêm trà sữa size L',           'Trà sữa hiện chỉ có 1 size, mong có size L cho người uống nhiều.',        'APPROVED', 55),
    ('user03@example.com', 'Bán thêm bánh cuốn',            'Bánh cuốn nóng chả quế buổi sáng sẽ rất hợp.',                             'REVIEWED', 40),
    ('user04@example.com', 'Thêm combo cơm văn phòng',      'Combo cơm + canh + nước uống giá ưu đãi cho dân văn phòng.',              'REVIEWED', 30),
    ('user05@example.com', 'Thêm nước dừa tươi',            'Mùa hè rất muốn uống nước dừa tươi nguyên trái.',                          'APPROVED', 50),
    ('user06@example.com', 'Bán rượu bia',                  'Có thể bán thêm bia lon để ăn kèm đồ nhậu không?',                         'REJECTED', 45),
    ('user07@example.com', 'Thêm món chay',                 'Nên có vài món chay vào ngày rằm, mùng một.',                              'PENDING',  5),
    ('user08@example.com', 'Thêm sữa chua uống',            'Sữa chua uống vị dâu cho trẻ em.',                                         'PENDING',  3),
    ('user09@example.com', 'Bánh mì que Hải Phòng',         'Bánh mì que pate cay nhỏ gọn, ăn vặt rất tiện.',                           'PENDING',  2),
    ('user10@example.com', 'Thêm topping phô mai',          'Cho phép chọn thêm topping phô mai cho trà sữa và gà rán.',               'REVIEWED', 20),
    ('user11@example.com', 'Lẩu mini 1 người',              'Lẩu thái mini cho 1 người ăn tối.',                                         'REJECTED', 35),
    ('user12@example.com', 'Thêm cà phê trứng',             'Cà phê trứng Hà Nội là món nên có trong menu đồ uống.',                    'APPROVED', 25),
    ('user13@example.com', 'Thêm salad ức gà',              'Món healthy cho người ăn kiêng.',                                           'PENDING',  1),
    ('user14@example.com', 'Bán kem que',                   'Kem que nhiều vị để giải nhiệt.',                                          'REJECTED', 15),
    ('user15@example.com', 'Thêm sinh tố dâu',              'Sinh tố dâu Đà Lạt mùa dâu.',                                               'PENDING',  0)
) AS v (email, title, description, status, days_ago)
JOIN users u ON u.email = v.email;

-- Lý do từ chối cho các suggestion REJECTED (cột thêm ở V6)
UPDATE suggestions s
SET reason_reject = v.reason
FROM (
  VALUES
    ('Bán rượu bia',     'Quán chưa có giấy phép kinh doanh đồ uống có cồn.'),
    ('Lẩu mini 1 người', 'Lẩu cần bếp riêng, khó giao hàng đảm bảo chất lượng.'),
    ('Bán kem que',      'Kem dễ tan khi giao hàng xa, chưa có thùng giữ lạnh chuyên dụng.')
) AS v (title, reason)
WHERE s.title = v.title AND s.status = 'REJECTED';

COMMIT;

-- Tổng kết
SELECT 'users' AS "table", COUNT(*) FROM users
UNION ALL SELECT 'user_addresses', COUNT(*) FROM user_addresses
UNION ALL SELECT 'products', COUNT(*) FROM products
UNION ALL SELECT 'orders', COUNT(*) FROM orders
UNION ALL SELECT 'order_items', COUNT(*) FROM order_items
UNION ALL SELECT 'evaluates', COUNT(*) FROM evaluates
UNION ALL SELECT 'carts', COUNT(*) FROM carts
UNION ALL SELECT 'cart_items', COUNT(*) FROM cart_items
UNION ALL SELECT 'suggestions', COUNT(*) FROM suggestions;
