package com.yaslebid.fileStorage.TestConfigAndData;

public class TestData {
    public static final String NEW_FILE_JSON = "{\"name\": \"testFile.avi\", \"size\" : 1010101}";
    public static final String NEW_FILE_UNKNOWN_TYPE_JSON = "{\"name\": \"testFile.tst\", \"size\" : 1010101}";
    public static final String EMPTY_FILE_NAME_JSON = "{\"name\": \"\", \"size\" : 1010101}";
    public static final String WITHOUT_FILE_NAME_JSON = "{\"size\" : 1010101}";
    public static final String NEGATIVE_FILE_SIZE_JSON = "{\"name\": \"testFile.avi\", \"size\" : -1010101}";
    public static final String WITHOUT_FILE_SIZE_JSON = "{\"name\": \"testFile.txt\"}";

    public static final String FAKE_FILE_ID = "ABCD-12345-ABCD-1234";
    public static final String[] TWO_TEST_TAGS_ARRAY = {"tag1", "tag2"};
}
