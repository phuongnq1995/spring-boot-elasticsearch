package org.phuongnq.elasticsearch.service;

import jakarta.transaction.Transactional;
import org.phuongnq.elasticsearch.domain.Tag;
import org.phuongnq.elasticsearch.model.TagDTO;
import org.phuongnq.elasticsearch.repos.PostRepository;
import org.phuongnq.elasticsearch.repos.TagRepository;
import org.phuongnq.elasticsearch.util.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    public TagService(final TagRepository tagRepository, final PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    public List<TagDTO> findAll() {
        final List<Tag> tags = tagRepository.findAll(Sort.by("id"));
        return tags.stream()
                .map(tag -> mapToDTO(tag, new TagDTO()))
                .toList();
    }

    public TagDTO get(final UUID id) {
        return tagRepository.findById(id)
                .map(tag -> mapToDTO(tag, new TagDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UUID create(final TagDTO tagDTO) {
        final Tag tag = new Tag();
        mapToEntity(tagDTO, tag);
        return tagRepository.save(tag).getId();
    }

    public void update(final UUID id, final TagDTO tagDTO) {
        final Tag tag = tagRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(tagDTO, tag);
        tagRepository.save(tag);
    }

    public void delete(final UUID id) {
        final Tag tag = tagRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        postRepository.findAllByTags(tag)
                .forEach(post -> post.getTags().remove(tag));
        tagRepository.delete(tag);
    }

    private TagDTO mapToDTO(final Tag tag, final TagDTO tagDTO) {
        tagDTO.setId(tag.getId());
        tagDTO.setName(tag.getName());
        return tagDTO;
    }

    private Tag mapToEntity(final TagDTO tagDTO, final Tag tag) {
        tag.setName(tagDTO.getName());
        return tag;
    }

    public boolean nameExists(final String name) {
        return tagRepository.existsByNameIgnoreCase(name);
    }

}
