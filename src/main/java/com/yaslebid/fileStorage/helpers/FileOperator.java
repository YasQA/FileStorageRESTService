package com.yaslebid.fileStorage.helpers;

import java.util.Optional;

public interface FileOperator {
    public Optional<String> getFileExtension(String fileName);
}
