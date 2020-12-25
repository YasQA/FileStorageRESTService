package com.yaslebid.fileStorage.controller.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;

@Document(indexName = "files")
public class File {
    @Id
    private String id;

    private String name;

    private Long size;

    @Field(type = Keyword)
    private Set<String> tags = new HashSet<>();

    public File() {}

    public File(String name, Long size) {
        this.name = name;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

}
