package com.yaslebid.fileStorage.controller.integration;

import com.yaslebid.fileStorage.helpers.TestFileOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DeleteFileTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private TestFileOperator testFileOperator;

    private String newFileId;

    @BeforeEach
    public void setup() throws Exception {
        newFileId = testFileOperator.mvcCreateNewFile();
    }

    @Test
    void deleteFile_andCheck_ifFileNotAvailableAnyMore() throws Exception {
        MvcResult deleteByIdResult = this.mvc.perform(delete("/file/{newFileId}", newFileId))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        MvcResult getByIdResult = this.mvc.perform(get("/file/{newFileId}", newFileId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void deleteNotExistentFile() throws Exception {
        final String uuid = UUID.randomUUID().toString();

        MvcResult deleteByIdResult = this.mvc.perform(delete("/file/{uuid}", uuid))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("file not found"))
                .andReturn();
    }
}
