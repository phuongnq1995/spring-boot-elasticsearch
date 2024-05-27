package org.phuongnq.elasticsearch.model;

import lombok.Getter;
import lombok.Setter;
import org.phuongnq.elasticsearch.document.PostDocument;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class PostPage extends PageImpl<PostDTO> {
    private final SearchHits<PostDocument> searchHits;
    private final List<Pair<String, Long>> tagAggregations;

    public PostPage(SearchHits<PostDocument> searchHits, Pageable pageable) {
        super(searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(PostDTO::fromDocument)
                .collect(Collectors.toList()), pageable, searchHits.getTotalHits());
        this.tagAggregations = ((ElasticsearchAggregations) searchHits.getAggregations())
                .get("type_aggregation")
                .aggregation()
                .getAggregate()
                .sterms()
                .buckets()
                .array()
                .stream()
                .map(stringTermsBucket -> Pair.of(stringTermsBucket.key().stringValue(), stringTermsBucket.docCount()))
                .collect(Collectors.toList());
        this.searchHits = searchHits;
    }
}
