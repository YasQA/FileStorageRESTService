package com.yaslebid.fileStorage.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yaslebid.fileStorage.TestConfigAndData.TestData;
import com.yaslebid.fileStorage.controller.model.File;
import com.yaslebid.fileStorage.repository.FilesRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest
class FileTagsRemoverByFileIdTest {
    @Autowired
    FileTagsRemover fileTagsRemover;

    @MockBean
    FilesRepository filesRepository;

    @Test
    void removeTagsFromFile_ExistentTags() {
        File fakeFile = new File("testName", 12345L);
        Arrays.stream(TestData.TWO_TEST_TAGS_ARRAY).forEach(fakeFile::addTag);

        when(filesRepository.findById(TestData.FAKE_FILE_ID))
                .thenReturn(Optional.of(fakeFile));

        when(filesRepository.save(fakeFile)).thenReturn(fakeFile);

        ObjectNode responseNode = fileTagsRemover.removeTags(TestData.FAKE_FILE_ID, TestData.TWO_TEST_TAGS_ARRAY);

        assertTrue(responseNode.get("success").asBoolean());
    }

    @Test
    void removeTagsFromFile_NotExistentFile() {
        when(filesRepository.findById(TestData.FAKE_FILE_ID))
                .thenReturn(Optional.empty());

        ObjectNode responseNode = fileTagsRemover.removeTags(TestData.FAKE_FILE_ID, TestData.TWO_TEST_TAGS_ARRAY);

        assertFalse(responseNode.get("success").asBoolean());
        Assertions.assertEquals("file not found", responseNode.get("error").textValue());
    }

    @Test
    void removeTagsFromFile_NotExistentTags() {
        File fakeFile = new File("testName", 12345L);

        when(filesRepository.findById(TestData.FAKE_FILE_ID))
                .thenReturn(Optional.of(fakeFile));

        ObjectNode responseNode = fileTagsRemover.removeTags(TestData.FAKE_FILE_ID, TestData.TWO_TEST_TAGS_ARRAY);

        assertFalse(responseNode.get("success").asBoolean());
        Assertions.assertEquals("tag not found on file", responseNode.get("error").textValue());
    }
}