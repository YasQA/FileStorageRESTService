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

    // Search on tags, must correspond all list of tags
    @Query("{\"terms_set\": {\"tags\": {\"terms\": ?0, " +
            "\"minimum_should_match_script\": {\"source\": \"params.num_terms\"}}}}")
    Page<File> findByFilteredTagQuery(JSONArray tags, Pageable pageable);

    // Search by file name with wildcard
    @Query("{\"bool\": {\"must\": " +
            "[{\"wildcard\": {\"name\": \"?0\" }}]}}")
    Page<File> findByNameContainingQ(String query, Pageable pageable);

    // Search on tags and file name with wildcard
    @Query("{\"bool\": {\"must\": " +
            "[{\"wildcard\": {\"name\": \"?0\" }}]," +
            " \"filter\": [{\"terms_set\": {\"tags\": {\"terms\": ?1, " +
            "\"minimum_should_match_script\": {\"source\": \"params.num_terms\"}}}}]}}")
    Page<File> findByNameContainingQandTags(String query, JSONArray tags, Pageable pageable);


}
