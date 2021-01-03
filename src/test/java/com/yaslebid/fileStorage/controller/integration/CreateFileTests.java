package com.yaslebid.fileStorage.controller.integration;

import com.yaslebid.fileStorage.TestConfigAndData.TestData;
import com.yaslebid.fileStorage.helpers.FileIdParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateFileTests {
    @Autowired
    private MockMvc mvc;

    private String newFileId;

    @BeforeAll
    public void setup() throws Exception {
        MvcResult saveResult = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.NEW_FILE_JSON))
                .andReturn();

        String content = saveResult.getResponse().getContentAsString();
        newFileId = FileIdParser.getIdFromCreateFileResponse(content);
    }

    @AfterAll
    public void teardown() throws Exception {
        MvcResult saveResult = this.mvc.perform(delete("/file/{newFileId}", newFileId)).andReturn();
    }

    @Test
    void saveSuccessfully_and_withCorrectBasicData() throws Exception {
        MvcResult getByIdResult = this.mvc.perform(get("/file/{newFileId}", newFileId))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newFileId))
                .andExpect(jsonPath("$.name").value("testFile.avi"))
                .andExpect(jsonPath("$.size").value("1010101"))
                .andReturn();
    }

    @Test
    void saveSuccessfully_and_withCorrectAutoTagData() throws Exception {
        MvcResult getByIdResult = this.mvc.perform(get("/file/{newFileId}", newFileId))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.tags").value("video"))
                .andReturn();
    }
}
