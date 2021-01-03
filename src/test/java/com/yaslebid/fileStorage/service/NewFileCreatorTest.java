package com.yaslebid.fileStorage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yaslebid.fileStorage.TestConfigAndData.TestData;
import com.yaslebid.fileStorage.controller.model.File;
import com.yaslebid.fileStorage.helpers.FileTypeResolver;
import com.yaslebid.fileStorage.helpers.TestFileOperator;
import com.yaslebid.fileStorage.repository.FilesRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static com.yaslebid.fileStorage.FileStorageRestServiceApplication.LOGGER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NewFileCreatorTest {
    @Autowired
    FileCreator fileCreator;

    @Autowired
    TestFileOperator testFileOperator;

    private File fakeFile;

    @AfterAll
    public void teardown() throws Exception {
        testFileOperator.mvcDeleteFile(fakeFile.getId());
    }

    @Test
    void createNewFile_correctData() {
        fakeFile = new File("testName", 12345L);

        ObjectNode responseNode = fileCreator.createFile(fakeFile);

        assertTrue(responseNode.get("ID").textValue().matches("[-_A-Za-z0-9]{20}"));
    }

    @Test
    void createNewFile_emptyName() {
        fakeFile = new File("", 12345L);

        ObjectNode responseNode = fileCreator.createFile(fakeFile);

        assertFalse(responseNode.get("success").asBoolean());
        Assertions.assertEquals("wrong or empty file name", responseNode.get("error").textValue());
    }

    @Test
    void createNewFile_noName() {
        fakeFile = new File();
        ObjectNode responseNode = fileCreator.createFile(fakeFile);

        assertFalse(responseNode.get("success").asBoolean());
        Assertions.assertEquals("wrong or empty file name", responseNode.get("error").textValue());
    }

    @Test
    void createNewFile_negativeSize() {
        fakeFile = new File("testFile", -12345L);

        ObjectNode responseNode = fileCreator.createFile(fakeFile);

        assertFalse(responseNode.get("success").asBoolean());
        Assertions.assertEquals("wrong or empty file size, should be not less than 0"
                , responseNode.get("error").textValue());
    }

}
