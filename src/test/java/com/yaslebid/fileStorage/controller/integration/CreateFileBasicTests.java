package com.yaslebid.fileStorage.controller.integration;

import com.yaslebid.fileStorage.TestConfigAndData.TestData;
import com.yaslebid.fileStorage.helpers.FileIdParser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CreateFileBasicTests {
    @Autowired
    private MockMvc mvc;

    @Test
    void saveSuccessfullyBasicTest() throws Exception {
        MvcResult result = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.NEW_FILE_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful()).andReturn();

        String content = result.getResponse().getContentAsString();
        String newFileId = FileIdParser.getIdFromCreateFileResponse(content);

        //assert generated id corresponds to Elasticsearch id pattern
        Assertions.assertTrue(newFileId.matches("[-_A-Za-z0-9]{20}"));
    }

    @Test
    void saveSuccessfully_and_withoutAutoTagData() throws Exception {
        MvcResult saveResult = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.NEW_FILE_UNKNOWN_TYPE_JSON))
                .andReturn();

        String content = saveResult.getResponse().getContentAsString();
        String newFileId = FileIdParser.getIdFromCreateFileResponse(content);

        MvcResult getByIdResult = this.mvc.perform(get("/file/{newFileId}", newFileId))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.tags", Matchers.empty()))
                .andReturn();
    }

    @Test
    void saveWithEmptyFileName() throws Exception  {
        MvcResult result = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.EMPTY_FILE_NAME_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("wrong or empty file name"))
                .andReturn();
    }

    @Test
    void saveWithoutFileName() throws Exception  {
        MvcResult result = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.WITHOUT_FILE_NAME_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("wrong or empty file name"))
                .andReturn();

    }

    @Test
    void saveWithNegativeFileSize() throws Exception  {
        MvcResult result = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.NEGATIVE_FILE_SIZE_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error")
                        .value("wrong or empty file size, should be not less than 0"))
                .andReturn();
    }

    @Test
    void saveWithoutFileSize() throws Exception  {
        MvcResult result = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.WITHOUT_FILE_SIZE_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error")
                        .value("wrong or empty file size, should be not less than 0"))
                .andReturn();
    }
}