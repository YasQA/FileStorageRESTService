package com.yaslebid.fileStorage.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface FileTagsCreator {
    ObjectNode addTags(String id, String[] tags);
}
