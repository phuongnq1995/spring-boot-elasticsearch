package org.phuongnq.elasticsearch.rest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.phuongnq.elasticsearch.model.TagDTO;
import org.phuongnq.elasticsearch.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(value = "/api/tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class TagResource {

    private final TagService tagService;

    public TagResource(final TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags() {
        return ResponseEntity.ok(tagService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getTag(@PathVariable(name = "id") final UUID id) {
        return ResponseEntity.ok(tagService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createTag(@RequestBody @Valid final TagDTO tagDTO) {
        final UUID createdId = tagService.create(tagDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UUID> updateTag(@PathVariable(name = "id") final UUID id,
                                          @RequestBody @Valid final TagDTO tagDTO) {
        tagService.update(id, tagDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteTag(@PathVariable(name = "id") final UUID id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
