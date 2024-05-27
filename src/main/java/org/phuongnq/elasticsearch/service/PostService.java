package org.phuongnq.elasticsearch.service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.phuongnq.elasticsearch.document.PostDocument;
import org.phuongnq.elasticsearch.domain.Post;
import org.phuongnq.elasticsearch.domain.Tag;
import org.phuongnq.elasticsearch.domain.User;
import org.phuongnq.elasticsearch.model.PostDTO;
import org.phuongnq.elasticsearch.model.PostPage;
import org.phuongnq.elasticsearch.model.PostRequest;
import org.phuongnq.elasticsearch.repos.PostRepository;
import org.phuongnq.elasticsearch.repos.TagRepository;
import org.phuongnq.elasticsearch.repos.UserRepository;
import org.phuongnq.elasticsearch.util.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public PostPage findAll(Pageable pageable, String search) {
        String queryString;

        if (StringUtils.isEmpty(search)) {

            queryString = StringQuery.MATCH_ALL;
        } else {

            queryString = """
                    {
                        "multi_match": {
                            "query": "%s",
                            "fields": ["title^2", "content"]
                        }
                    }
                    """;
        }

        log.info("query: {}", queryString);

        var aggregation = new Aggregation.Builder()
                .terms(TermsAggregation.of(builder -> builder.field("tags")))
                .build();

        Query query = NativeQuery.builder()
                .withQuery(StringQuery.builder(String.format(queryString, search)).build())
                .withAggregation("type_aggregation", aggregation)
                .withPageable(pageable)
                .build();

        SearchHits<PostDocument> searchHits = elasticsearchOperations.search(query, PostDocument.class);

        return new PostPage(searchHits, pageable);
    }

    public PostDTO get(final UUID id) {
        return postRepository.findById(id)
                .map(post -> mapEntityToDTO(post, new PostDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UUID create(final PostRequest postDTO) {
        final Post post = new Post();
        mapToEntity(postDTO, post);
        post.domainOperation();
        return postRepository.save(post).getId();
    }

    public void update(final UUID id, final PostRequest postDTO) {
        final Post post = postRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(postDTO, post);
        postRepository.save(post);
    }

    public void delete(final UUID id) {
        postRepository.deleteById(id);
    }

    private PostDTO mapEntityToDTO(final Post post, final PostDTO postDTO) {
        postDTO.setId(post.getId());
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setUser(post.getUser() == null ? null : post.getUser().getUsername());
        postDTO.setTags(post.getTags().stream()
                .map(Tag::getName)
                .toList());
        return postDTO;
    }

    private void mapToEntity(final PostRequest postDTO, final Post post) {
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        final User user = postDTO.getUser() == null ? null : userRepository.findById(postDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        post.setUser(user);
        final List<Tag> tags = tagRepository.findAllById(
                postDTO.getTags() == null ? Collections.emptyList() : postDTO.getTags());
        if (tags.size() != (postDTO.getTags() == null ? 0 : postDTO.getTags().size())) {
            throw new NotFoundException("one of tags not found");
        }
        post.setTags(new HashSet<>(tags));
    }

}
