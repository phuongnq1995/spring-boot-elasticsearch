package org.phuongnq.elasticsearch.repos;

import org.phuongnq.elasticsearch.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface TagRepository extends JpaRepository<Tag, UUID> {

    boolean existsByNameIgnoreCase(String name);

}
