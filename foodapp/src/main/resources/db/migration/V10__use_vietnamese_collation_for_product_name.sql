-- Sort product names in Vietnamese alphabetical order (a, ă, â, b, ..., d, đ, ...).
-- The database default collation (en_US.utf8) puts accented letters after unaccented ones,
-- e.g. "Burger" before "Bánh". ICU "vi-x-icu" is deterministic, so LIKE/equality still work.
ALTER TABLE products ALTER COLUMN name TYPE VARCHAR(150) COLLATE "vi-x-icu";
