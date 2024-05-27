package org.phuongnq.elasticsearch.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phuongnq.elasticsearch.document.PostDocument;
import org.phuongnq.elasticsearch.domain.Post;
import org.phuongnq.elasticsearch.repos.PostRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexerRunner implements ApplicationRunner {
    private final ElasticsearchOperations elasticsearchOperations;
    private final PostRepository repository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("### Start to reindexing... ###");

        if (elasticsearchOperations.indexOps(PostDocument.class).exists()) {
            elasticsearchOperations.indexOps(PostDocument.class).delete();
            elasticsearchOperations.indexOps(PostDocument.class).create();
            elasticsearchOperations.indexOps(PostDocument.class).putMapping();
        }

        List<IndexQuery> indexQueries = repository.findAll()
                .stream()
                .map(this::indexRequest)
                .collect(Collectors.toList());

        List<IndexedObjectInformation> informationList =
                elasticsearchOperations.bulkIndex(indexQueries, PostDocument.class);

        log.info("### Finished reindexing {} documents. ###", informationList.size());
    }

    private IndexQuery indexRequest(Post post) {
        return new IndexQueryBuilder()
                .withObject(post.toDocument())
                .withId(String.valueOf(post.getId()))
                .build();
    }
}
