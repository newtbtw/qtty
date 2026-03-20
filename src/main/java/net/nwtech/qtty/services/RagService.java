package net.nwtech.qtty.services;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {

    private final VectorStore usersVectorStore;
    private final VectorStore moviesVectorStore;

    public RagService(@Qualifier("users-vector-store") VectorStore usersVectorStore, @Qualifier("movies-vector-store") VectorStore moviesVectorStore) {
        this.usersVectorStore = usersVectorStore;
        this.moviesVectorStore = moviesVectorStore;
    }

    public List<Document> findUserInfo(Long discordUserId, String info){
        return usersVectorStore.similaritySearch(SearchRequest.builder()
                        .similarityThreshold(0.6f)
                        .topK(3)
                        .query(info)
                        .build()
        );
    }

    public List<Document> findMoviesInfo(Long discordUserId, String info){
        return usersVectorStore.similaritySearch(SearchRequest.builder()
                .similarityThreshold(0.6f)
                .topK(3)
                .query(info)
                .build()
        );
    }

}