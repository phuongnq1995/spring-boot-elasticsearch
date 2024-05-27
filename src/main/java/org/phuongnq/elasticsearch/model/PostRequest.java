package org.phuongnq.elasticsearch.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class PostRequest {

    private UUID id;

    @Size(max = 255)
    private String title;

    @Size(max = 5000)
    private String content;

    private UUID user;

    private List<UUID> tags;
}
