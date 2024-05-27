package org.phuongnq.elasticsearch.service;

import lombok.RequiredArgsConstructor;
import org.phuongnq.elasticsearch.util.PostDomainEvent;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class PostSearchHandler {

    private final ElasticsearchOperations elasticsearchOperations;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleTournamentEndedEvent(PostDomainEvent event) {
        elasticsearchOperations.save(event.getPostDocument());
    }
}
