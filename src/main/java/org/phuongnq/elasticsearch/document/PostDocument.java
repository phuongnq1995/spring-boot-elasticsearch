package org.phuongnq.elasticsearch.document;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.UUID;

@Data
@Builder
@Document(indexName = "post", alwaysWriteMapping = true)
public class PostDocument {
    @Id
    private UUID id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword)
    private String user;

    @Field(type = FieldType.Keyword)
    private String[] tags;
}
