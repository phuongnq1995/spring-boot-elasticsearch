package org.phuongnq.elasticsearch.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Valid
@ConfigurationProperties(prefix = "project")
public class ProjectProperties {

    @NotNull
    @Valid
    private Elasticsearch elasticsearch;

    @Getter
    @Setter
    @Valid
    public static class Elasticsearch {
        @NotBlank
        private String sslFingerprintCa;
    }
}
