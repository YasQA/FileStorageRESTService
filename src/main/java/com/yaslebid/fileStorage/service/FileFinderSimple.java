package com.yaslebid.fileStorage.service;

import com.yaslebid.fileStorage.controller.model.File;

public interface FileFinderSimple {
    File getFileById(String id);
}
