package com.yaslebid.fileStorage.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public interface FileFinderParametrized {
    ObjectNode searchFiles(List<String> tags, String q, int size, int page);
}
