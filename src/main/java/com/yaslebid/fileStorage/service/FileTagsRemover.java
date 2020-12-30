package com.yaslebid.fileStorage.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface FileTagsRemover {
    ObjectNode removeTags(String id, String[] tags);
}
