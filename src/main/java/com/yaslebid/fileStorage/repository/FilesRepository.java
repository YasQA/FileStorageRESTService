package com.yaslebid.fileStorage.repository;

import org.json.JSONArray;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.yaslebid.fileStorage.controller.model.File;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FilesRepository extends ElasticsearchRepository<File, String> {
    public List<File> findByTagsIn(Collection<String> tags);

    @Query("{\"bool\": {\"must\": {\"match_all\": {}}, \"filter\": {\"terms\": {\"tags\": ?0}}}}")
    Page<File> findByFilteredTagQuery(JSONArray tags, Pageable pageable);
}
