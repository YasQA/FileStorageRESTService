package com.yaslebid.fileStorage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yaslebid.fileStorage.controller.model.File;
import com.yaslebid.fileStorage.repository.FilesRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.yaslebid.fileStorage.FileStorageRestServiceApplication.LOGGER;

@Component
public class FileTagsCreatorByFileId implements FileTagsCreator {
    private final FilesRepository filesRepository;

    public FileTagsCreatorByFileId(FilesRepository filesRepository) {
        this.filesRepository = filesRepository;
    }

    public ObjectNode addTags(String id, String[] tags) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        if (filesRepository.findById(id).isEmpty()) {
            objectNode.put("success", false);
            objectNode.put("error", "file not found");
            LOGGER.error("Failed to add tags to file, file not found");
            return objectNode;
        }

        try {
            File file = filesRepository.findById(id).get();
            Arrays.stream(tags).forEach(file::addTag);
            filesRepository.save(file);
            objectNode.put("success", true);
            LOGGER.info("Tags added to file: " + file.getName());

        } catch (Exception exception) {
            objectNode.put("error", "exception: " + exception.getMessage());
            LOGGER.error("Failed to add tags to file: " + exception.getMessage());
        }
        return objectNode;
    }
}
