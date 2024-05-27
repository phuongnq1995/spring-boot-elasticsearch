package org.phuongnq.elasticsearch.repos;

import org.phuongnq.elasticsearch.domain.Post;
import org.phuongnq.elasticsearch.domain.Tag;
import org.phuongnq.elasticsearch.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface PostRepository extends JpaRepository<Post, UUID> {

    Post findFirstByUser(User user);

    Post findFirstByTags(Tag tag);

    List<Post> findAllByTags(Tag tag);

}
