package com.yaslebid.fileStorage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.yaslebid.fileStorage.controller.model.File;
import com.yaslebid.fileStorage.repository.FilesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;

import static com.yaslebid.fileStorage.FileStorageRestServiceApplication.LOGGER;

public class FileFinderFiltered implements FileFinderParametrized {
    private final FilesRepository filesRepository;

    public FileFinderFiltered(FilesRepository filesRepository) {
        this.filesRepository = filesRepository;
    }

    public ObjectNode searchFiles(List<String> tags, String q, int size, int page) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        String stringTags = new Gson().toJson(tags);
        Page<File> queryResult;
        Pageable pageToRespond = PageRequest.of(page, size);

        try {
            if ((tags != null && tags.size() > 0) && (q != null && !q.isEmpty())) {
                queryResult = filesRepository
                        .findByNameContainingQandTags("*" + q + "*", stringTags, pageToRespond);
                LOGGER.info("Both 'q=' and 'tags=' provided in request path, query successfully completed");
            } else if (tags != null && tags.size() > 0) {
                queryResult = filesRepository.findByFilteredTagQuery(stringTags, pageToRespond);
                LOGGER.info("'q=' not provided, returns files with ALL required 'tags=', query successfully completed");
            } else if (q != null && !q.isEmpty()) {
                queryResult = filesRepository.findByNameContainingQ("*" + q + "*", pageToRespond);
                LOGGER.info("'q=' provided, 'tags=' not provided in request path, query successfully completed");
            } else {
                queryResult = filesRepository.findAll(pageToRespond);
                LOGGER.info("'tags=' and 'q=' not provided, list of all files returned, query successfully completed");
            }

            List<File> resultList = queryResult.getContent();
            long total = queryResult.getTotalElements();
            objectNode.put("total", total);
            objectNode.putPOJO("page", resultList);

        } catch (Exception exception) {
            objectNode.put("error", "exception: " + exception.getMessage());
            LOGGER.error("Fails to get list of files according to the request" + exception.getMessage());
        }
        return objectNode;
    }
}
