package com.yaslebid.fileStorage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yaslebid.fileStorage.controller.model.File;
import com.yaslebid.fileStorage.repository.FilesRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

import static com.yaslebid.fileStorage.FileStorageRestServiceApplication.LOGGER;

@Component
public class FileTagsRemoverByFileId implements FileTagsRemover {
    private final FilesRepository filesRepository;

    public FileTagsRemoverByFileId(FilesRepository filesRepository) {
        this.filesRepository = filesRepository;
    }

    public ObjectNode removeTags(String id, String[] tags) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        Optional<File> optionalFile = filesRepository.findById(id);

        if (optionalFile.isEmpty()) {
            objectNode.put("success", false);
            objectNode.put("error", "file not found");
            LOGGER.error("Failed to delete tags from file, file not found");
            return objectNode;
        } else if (!optionalFile.get().getTags().containsAll(Arrays.asList(tags))) {
            objectNode.put("success", false);
            objectNode.put("error", "tag not found on file");
            LOGGER.error("Fails to delete tags from file, tag(-s) not found");
            return objectNode;
        }

        try {
            File file = optionalFile.get();
            file.getTags().removeAll(Arrays.asList(tags));
            filesRepository.save(file);
            objectNode.put("success", true);
            LOGGER.info("Specified tags deleted for file: " + file.getName());

        } catch (Exception exception) {
            objectNode.put("error", "exception: " + exception.getMessage());
            LOGGER.error("Fails to delete tags from file: " + exception.getMessage());
        }
        return objectNode;
    }
}
