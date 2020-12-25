package com.yaslebid.fileStorage.helpers;

import com.yaslebid.fileStorage.controller.model.File;
import com.yaslebid.fileStorage.helpers.fileExtensions.AudioFileExtensions;
import com.yaslebid.fileStorage.helpers.fileExtensions.DocumentFileExtensions;
import com.yaslebid.fileStorage.helpers.fileExtensions.ImageFileExtensions;
import com.yaslebid.fileStorage.helpers.fileExtensions.VideoFileExtensions;

public class FileTypeResolver {
    public String identifyFileType(File file) {
        FileOperator fileOperator = new FileTypeOperator();
        if (fileOperator.getFileExtension(file.getName()).isEmpty()) {
            return "unknown";
        }

        String fileNameExtension = fileOperator.getFileExtension(file.getName()).get();

        for (ImageFileExtensions extension : ImageFileExtensions.values()) {
            if (fileNameExtension.equalsIgnoreCase(extension.toString()))
                return "image";
        }

        for (VideoFileExtensions extension : VideoFileExtensions.values()) {
            if (fileNameExtension.equalsIgnoreCase(extension.toString()))
                return "video";
        }

        for (DocumentFileExtensions extension : DocumentFileExtensions.values()) {
            if (fileNameExtension.equalsIgnoreCase(extension.toString()))
                return "document";
        }

        for (AudioFileExtensions extension : AudioFileExtensions.values()) {
            if (fileNameExtension.equalsIgnoreCase(extension.toString()))
                return "audio";
        }

        return "unknown";
    }
}
