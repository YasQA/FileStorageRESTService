package com.yaslebid.fileStorage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yaslebid.fileStorage.repository.FilesRepository;
import org.springframework.stereotype.Component;

import static com.yaslebid.fileStorage.FileStorageRestServiceApplication.LOGGER;

@Component
public class FileRemoverById implements FileRemover {

    private final FilesRepository filesRepository;

    public FileRemoverById(FilesRepository filesRepository) {
        this.filesRepository = filesRepository;
    }

    public ObjectNode deleteFileById(String id) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        if (filesRepository.findById(id).isEmpty()) {
            objectNode.put("success", false);
            objectNode.put("error", "file not found");
            return objectNode;
        }

        try {
            filesRepository.deleteById(id);
            objectNode.put("success", true);
            LOGGER.error("File with id: " + id + " deleted successfully");
        } catch (Exception exception) {
            objectNode.put("error", "exception: " + exception.getMessage());
            LOGGER.error("Failed to delete file: " + exception.getMessage());
        }
        return objectNode;
    }


}
