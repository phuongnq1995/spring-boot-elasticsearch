package org.phuongnq.elasticsearch.config;

import lombok.RequiredArgsConstructor;
import org.phuongnq.elasticsearch.properties.ProjectProperties;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
@RequiredArgsConstructor
public class ElasticSearchConfiguration extends ElasticsearchConfiguration {
    private final ElasticsearchProperties elasticsearchProperties;
    private final ProjectProperties projectProperties;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchProperties.getUris().toArray(String[]::new))
                .usingSsl(projectProperties.getElasticsearch().getSslFingerprintCa()) //add the generated sha-256 fingerprint
                .withBasicAuth(elasticsearchProperties.getUsername(), elasticsearchProperties.getPassword()) //add your username and password
                .build();
    }

}