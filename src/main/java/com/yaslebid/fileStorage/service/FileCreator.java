package com.yaslebid.fileStorage.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yaslebid.fileStorage.controller.model.File;

public interface FileCreator {
    ObjectNode createFile (File file);
}
