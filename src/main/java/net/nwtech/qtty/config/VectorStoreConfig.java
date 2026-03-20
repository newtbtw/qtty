package net.nwtech.qtty.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class VectorStoreConfig {

    private final JdbcTemplate jdbcTemplate;
    private final OpenAiEmbeddingModel openAiEmbeddingModel;

    @Bean
    @Qualifier("users-vector-store")
    public VectorStore usersVectorStore() {
        return PgVectorStore.builder(jdbcTemplate, openAiEmbeddingModel).vectorTableName("users").build();
    }

    @Bean
    @Qualifier("movies-vector-store")
    public VectorStore moviesVectorStore() {
        return PgVectorStore.builder(jdbcTemplate, openAiEmbeddingModel).vectorTableName("movies").build();
    }
}
