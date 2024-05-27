package org.phuongnq.elasticsearch.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.phuongnq.elasticsearch.document.PostDocument;
import org.phuongnq.elasticsearch.util.PostDomainEvent;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Post {
    @Transient
    private final Collection<PostDomainEvent> postDomainEvents = new ArrayList<>();

    @Id
    @Column(nullable = false, updatable = false)
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    private UUID id;

    @Column
    private String title;

    @Column(length = 5000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "PostTag",
            joinColumns = @JoinColumn(name = "postId"),
            inverseJoinColumns = @JoinColumn(name = "tagId")
    )
    private Set<Tag> tags;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

    public void domainOperation() {
        postDomainEvents.add(new PostDomainEvent(toDocument()));
    }

    public PostDocument toDocument() {
        return PostDocument.builder()
                .title(getTitle())
                .content(getContent())
                .user(getUser().getUsername())
                .tags(getTags()
                        .stream()
                        .map(Tag::getName)
                        .toArray(String[]::new))
                .build();
    }

    @DomainEvents
    public Collection<PostDomainEvent> events() {
        return postDomainEvents;
    }

    @AfterDomainEventPublication
    public void clearEvents() {
        postDomainEvents.clear();
    }
}
