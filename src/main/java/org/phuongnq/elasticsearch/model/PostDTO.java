package org.phuongnq.elasticsearch.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.phuongnq.elasticsearch.document.PostDocument;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {

    private UUID id;

    @Size(max = 255)
    private String title;

    @Size(max = 5000)
    private String content;

    private String user;

    private List<String> tags;

    public static PostDTO fromDocument(PostDocument document) {
        return new PostDTO(document.getId(), document.getTitle(), document.getContent(), document.getUser(),
                Arrays.asList(document.getTags()));
    }
}
