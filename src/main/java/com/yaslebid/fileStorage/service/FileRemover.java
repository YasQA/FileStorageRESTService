package com.yaslebid.fileStorage.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface FileRemover {
    ObjectNode deleteFileById(String id);
}
