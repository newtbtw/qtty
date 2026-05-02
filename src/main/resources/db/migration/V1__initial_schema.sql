CREATE TABLE qtty_user (
    id          BIGSERIAL PRIMARY KEY,
    discord_id  BIGINT       NOT NULL UNIQUE,
    username    VARCHAR(255)
);

CREATE TABLE dc_guild (
    id                BIGSERIAL PRIMARY KEY,
    discord_id        BIGINT       NOT NULL UNIQUE,
    allowed           BOOLEAN,
    name              VARCHAR(255),
    audit_channel_id  BIGINT,
    movies_channel_id BIGINT
);

CREATE TABLE dc_role (
    id         BIGSERIAL PRIMARY KEY,
    discord_id BIGINT       NOT NULL UNIQUE,
    role_name  VARCHAR(255) NOT NULL,
    guild_id   BIGINT       NOT NULL,
    CONSTRAINT fk_dc_role_guild FOREIGN KEY (guild_id) REFERENCES dc_guild(id)
);

CREATE TABLE user_guild_profile (
    id                    BIGSERIAL PRIMARY KEY,
    user_id               BIGINT,
    guild_id              BIGINT,
    guild_user_nick_name  VARCHAR(255),
    CONSTRAINT fk_ugp_user FOREIGN KEY (user_id) REFERENCES qtty_user(id),
    CONSTRAINT fk_ugp_guild FOREIGN KEY (guild_id) REFERENCES dc_guild(id)
);

CREATE TABLE user_guild_profile_roles (
    user_guild_profile_id BIGINT NOT NULL,
    roles_id              BIGINT NOT NULL,
    CONSTRAINT fk_ugp_roles_profile FOREIGN KEY (user_guild_profile_id) REFERENCES user_guild_profile(id),
    CONSTRAINT fk_ugp_roles_role FOREIGN KEY (roles_id) REFERENCES dc_role(id)
);

CREATE TABLE movie (
    id                        BIGSERIAL PRIMARY KEY,
    discord_rating_message_id BIGINT UNIQUE,
    title                     VARCHAR(255),
    synopsis                  VARCHAR(255),
    image_path                VARCHAR(255),
    release_date              VARCHAR(255)
);

CREATE TABLE rating_record (
    id       BIGSERIAL PRIMARY KEY,
    user_id  BIGINT,
    rating   DOUBLE PRECISION,
    movie_id BIGINT,
    CONSTRAINT fk_rating_record_user FOREIGN KEY (user_id) REFERENCES qtty_user(id),
    CONSTRAINT fk_rating_record_movie FOREIGN KEY (movie_id) REFERENCES movie(id)
);

CREATE TABLE movie_ratings (
    movie_id   BIGINT NOT NULL,
    ratings_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_movie_ratings_movie FOREIGN KEY (movie_id) REFERENCES movie(id),
    CONSTRAINT fk_movie_ratings_rating FOREIGN KEY (ratings_id) REFERENCES rating_record(id)
);

CREATE TABLE dc_channel (
    id         BIGSERIAL PRIMARY KEY,
    discord_id BIGINT       NOT NULL UNIQUE,
    name       VARCHAR(255),
    type       VARCHAR(255) CHECK (type IN ('VOICE', 'TEXT'))
);

CREATE TABLE dc_channel_permissions (
    id BIGSERIAL PRIMARY KEY
);
