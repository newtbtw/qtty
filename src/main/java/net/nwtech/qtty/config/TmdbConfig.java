package net.nwtech.qtty.config;

import info.movito.themoviedbapi.TmdbApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TmdbConfig {

    @Value("${tmdb.key}")
    private String key;

    @Bean
    public TmdbApi api() {
        return new TmdbApi(key);
    }

}
