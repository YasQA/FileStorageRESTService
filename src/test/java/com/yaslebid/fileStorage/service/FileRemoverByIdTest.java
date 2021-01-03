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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
class FileRemoverByIdTest {
    @Autowired
    FileRemover fileRemover;

    @MockBean
    FilesRepository filesRepository;

    @Test
    void removeFileById_Existent() {
        when(filesRepository.findById(TestData.FAKE_FILE_ID))
                .thenReturn(Optional.of(new File("testName", 12345L)));

        doNothing().when(filesRepository).deleteById(TestData.FAKE_FILE_ID);

        ObjectNode responseNode = fileRemover.deleteFileById(TestData.FAKE_FILE_ID);
        assertTrue(responseNode.get("success").asBoolean());
    }

    @Test
    void removeFileById_NotExistent() {
        when(filesRepository.findById(TestData.FAKE_FILE_ID))
                .thenReturn(Optional.empty());

        ObjectNode responseNode = fileRemover.deleteFileById(TestData.FAKE_FILE_ID);
        assertFalse(responseNode.get("success").asBoolean());
        Assertions.assertEquals("file not found", responseNode.get("error").textValue());

    }


}
