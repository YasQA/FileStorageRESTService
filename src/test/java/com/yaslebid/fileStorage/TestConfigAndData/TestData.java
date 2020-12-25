package com.yaslebid.fileStorage.TestConfigAndData;

public class TestData {
    public static final String newFileJson = "{\"name\": \"testFile.avi\", \"size\" : 1010101}";
    public static final String emptyFileNameJson = "{\"name\": \"\", \"size\" : 1010101}";
    public static final String withoutFileNameJson = "{\"size\" : 1010101}";
    public static final String negativeFileSizeJson = "{\"name\": \"testFile.avi\", \"size\" : -1010101}";
    public static final String withoutFileSizeJson = "{\"name\": \"testFile.txt\"}";

    public static final String wrongFileNameResponse
            = "{\"success\":false,\"error\":\"wrong or empty file name\"}";
    public static final String wrongFileSizeResponse
            = "{\"success\":false,\"error\":\"wrong or empty file size, should be not less than 0\"}";

}
