package com.yaslebid.fileStorage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yaslebid.fileStorage.controller.model.File;
import com.yaslebid.fileStorage.helpers.FileTypeResolver;
import com.yaslebid.fileStorage.repository.FilesRepository;
import org.springframework.stereotype.Component;

import static com.yaslebid.fileStorage.FileStorageRestServiceApplication.LOGGER;

@Component
public class NewFileCreator implements FileCreator {
    private final FilesRepository filesRepository;

    public NewFileCreator(FilesRepository filesRepository) {
        this.filesRepository = filesRepository;
    }

    public ObjectNode createFile (File file) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        file.setId(null);

        if (!ifFileNameCorrect(file)) {
            objectNode.put("success", false);
            String message = "wrong or empty file name";
            objectNode.put("error", message);
            LOGGER.error(message);
            return objectNode;
        }

        if (!ifFileSizeCorrect(file)) {
            objectNode.put("success", false);
            String message = "wrong or empty file size, should be not less than 0";
            objectNode.put("error", message);
            LOGGER.error(message);
            return objectNode;
        }

        try {
            String fileType = new FileTypeResolver().identifyFileType(file);
            if (!fileType.equals("unknown")) {
                file.addTag(fileType);
            }
            filesRepository.save(file);
            objectNode.put("ID", file.getId());

            LOGGER.info("file: " + file.getName() + ", id: " + file.getId() + " created successfully");

        } catch (Exception exception) {
            objectNode.put("error", "exception: " + exception.getMessage());
            LOGGER.error("Failed to create file: " + exception.getMessage());
        }
        return objectNode;
    }


    private boolean ifFileNameCorrect(File file) {
        return file.getName() != null && !file.getName().equals("");
    }

    private boolean ifFileSizeCorrect(File file) {
        return file.getSize() != null && file.getSize() >= 0;
    }
}
