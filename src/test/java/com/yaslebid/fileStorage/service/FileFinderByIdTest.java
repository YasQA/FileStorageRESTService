package com.yaslebid.fileStorage.service;

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
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest
class FileFinderByIdTest {

    @Autowired
    FileFinderSimple fileFinderById;

    @MockBean
    FilesRepository filesRepository;

    @Test
    void findFileById_existent() {
        when(filesRepository.findById(TestData.FAKE_FILE_ID))
                .thenReturn(Optional.of(new File("testName", 12345L)));

        File resultFile = fileFinderById.getFileById(TestData.FAKE_FILE_ID);
        Assertions.assertEquals("testName", resultFile.getName());
        Assertions.assertEquals(12345L, resultFile.getSize());

    }
    @Test
    void findFileById_notExistent() {
        when(filesRepository.findById(TestData.FAKE_FILE_ID))
                .thenReturn(Optional.empty());

        File resultFile = fileFinderById.getFileById(TestData.FAKE_FILE_ID);
        assertNull(resultFile.getName());
        assertNull(resultFile.getSize());
    }

}
