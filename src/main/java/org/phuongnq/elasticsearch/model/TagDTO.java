package org.phuongnq.elasticsearch.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TagDTO {

    private UUID id;

    @NotNull
    @Size(max = 255)
    @TagNameUnique
    private String name;

}
