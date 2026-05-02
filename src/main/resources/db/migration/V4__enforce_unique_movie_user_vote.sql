DELETE FROM rating_record rr
USING rating_record duplicate
WHERE rr.movie_id = duplicate.movie_id
  AND rr.user_id = duplicate.user_id
  AND rr.id < duplicate.id
  AND rr.movie_id IS NOT NULL
  AND rr.user_id IS NOT NULL;

ALTER TABLE rating_record
    ADD CONSTRAINT uq_rating_record_movie_user UNIQUE (movie_id, user_id);
