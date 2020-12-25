package com.yaslebid.fileStorage.helpers;

import java.util.Optional;

public class FileTypeOperator implements FileOperator{
    public Optional<String> getFileExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1));
    }
}
