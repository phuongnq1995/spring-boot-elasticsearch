package org.phuongnq.elasticsearch.rest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.phuongnq.elasticsearch.document.PostDocument;
import org.phuongnq.elasticsearch.model.PostDTO;
import org.phuongnq.elasticsearch.model.PostPage;
import org.phuongnq.elasticsearch.model.PostRequest;
import org.phuongnq.elasticsearch.service.PostService;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(value = "/api/posts", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostResource {

    private final PostService postService;

    public PostResource(final PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    @PageableAsQueryParam
    public ResponseEntity<PostPage> getAllPosts(Pageable pageable, @RequestParam(name = "q", required = false) String search) {
        return ResponseEntity.ok(postService.findAll(pageable, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable(name = "id") final UUID id) {
        return ResponseEntity.ok(postService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createPost(@RequestBody @Valid final PostRequest postDTO) {
        final UUID createdId = postService.create(postDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UUID> updatePost(@PathVariable(name = "id") final UUID id,
                                           @RequestBody @Valid final PostRequest postDTO) {
        postService.update(id, postDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePost(@PathVariable(name = "id") final UUID id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
