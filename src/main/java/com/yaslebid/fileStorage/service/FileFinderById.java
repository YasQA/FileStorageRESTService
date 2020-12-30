package com.yaslebid.fileStorage.service;

import com.yaslebid.fileStorage.controller.model.File;
import com.yaslebid.fileStorage.repository.FilesRepository;

import java.util.Optional;

import static com.yaslebid.fileStorage.FileStorageRestServiceApplication.LOGGER;

public class FileFinderById implements FileFinderSimple {
    private final FilesRepository filesRepository;

    public FileFinderById(FilesRepository filesRepository) {
        this.filesRepository = filesRepository;
    }

    public File getFileById(String id) {
        Optional<File> file = filesRepository.findById(id);
        if (file.isPresent()) {
            LOGGER.info("File with id: " + id + " found");
            return file.get();
        } else {
            LOGGER.error("Failed to get file: Not Found");
            return new File();
        }
    }
}
