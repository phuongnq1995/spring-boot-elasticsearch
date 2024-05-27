package org.phuongnq.elasticsearch.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.phuongnq.elasticsearch.document.PostDocument;

@RequiredArgsConstructor
@Getter
public class PostDomainEvent {
    private final PostDocument postDocument;
}
