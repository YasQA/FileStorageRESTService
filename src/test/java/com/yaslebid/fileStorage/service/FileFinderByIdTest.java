package com.yaslebid.fileStorage.service;

import com.yaslebid.fileStorage.TestConfigAndData.TestData;
import com.yaslebid.fileStorage.controller.model.File;
import com.yaslebid.fileStorage.repository.FilesRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static com.yaslebid.fileStorage.FileStorageRestServiceApplication.LOGGER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

//@ExtendWith(SpringExtension.class)
//@RunWith(SpringRunner.class)
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
