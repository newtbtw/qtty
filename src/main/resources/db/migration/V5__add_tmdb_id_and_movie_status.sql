ALTER TABLE movie
    ADD COLUMN tmdb_id BIGINT,
    ADD COLUMN status VARCHAR(32);

UPDATE movie
SET status = CASE
    WHEN discord_rating_message_id IS NOT NULL THEN 'PUBLISHED'
    ELSE 'SEARCHED'
END
WHERE status IS NULL;

ALTER TABLE movie
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE movie
    ADD CONSTRAINT uq_movie_tmdb_id UNIQUE (tmdb_id);
