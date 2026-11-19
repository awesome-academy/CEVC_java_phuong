-- One evaluate per (product, user).
-- The old bug let a user post a new evaluate after the admin hid theirs, so some pairs may have
-- two rows; findByProductIdAndUserId would then fail with 500.

-- 1. Keep one row per pair: a hidden one first (keeps the admin's moderation), otherwise the newest
DELETE FROM evaluates e
USING (
    SELECT id,
           ROW_NUMBER() OVER (PARTITION BY product_id, user_id
                              ORDER BY is_show ASC, created_at DESC, id DESC) AS rn
    FROM evaluates
) ranked
WHERE e.id = ranked.id
  AND ranked.rn > 1;

-- 2. Let the database reject any new duplicate
ALTER TABLE evaluates
    ADD CONSTRAINT uk_evaluates_product_user UNIQUE (product_id, user_id);

-- 3. Recalculate every product average from its visible evaluates (same formula as ProductRatingService)
UPDATE products p
SET average_rating = COALESCE((SELECT AVG(e.rating) FROM evaluates e WHERE e.product_id = p.id AND e.is_show), 0);
