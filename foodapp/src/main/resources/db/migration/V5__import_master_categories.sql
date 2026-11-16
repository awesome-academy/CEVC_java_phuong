-- V5__import_master_categories.sql
-- Default category tree for a Foods & Drinks shop (admin can still edit/delete later)

-- Parent categories
INSERT INTO
  categories (name, priority)
VALUES
  ('Đồ ăn', 1),
  ('Đồ uống', 2);

-- Child categories of "Đồ ăn"
INSERT INTO
  categories (parent_id, name, priority)
SELECT p.id, v.name, v.priority
FROM (
  VALUES
    ('Cơm', 1),
    ('Bún & Phở', 2),
    ('Bánh mì', 3),
    ('Đồ ăn nhanh', 4),
    ('Ăn vặt', 5),
    ('Tráng miệng', 6)
) AS v (name, priority)
JOIN categories p ON p.name = 'Đồ ăn' AND p.parent_id IS NULL;

-- Child categories of "Đồ uống"
INSERT INTO
  categories (parent_id, name, priority)
SELECT p.id, v.name, v.priority
FROM (
  VALUES
    ('Cà phê', 1),
    ('Trà & Trà sữa', 2),
    ('Nước ép & Sinh tố', 3),
    ('Nước giải khát', 4)
) AS v (name, priority)
JOIN categories p ON p.name = 'Đồ uống' AND p.parent_id IS NULL;
