package com.yaslebid.fileStorage.controller;

import com.yaslebid.fileStorage.TestConfigAndData.TestData;
import com.yaslebid.fileStorage.helpers.FileIdParser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class CreateFileBasicTests {
    @Autowired
    private MockMvc mvc;

    @Test
    void saveSuccessfullyBasicTest() throws Exception {
        MvcResult result = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.newFileJson))
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
                .content(TestData.newFileUnknownTypeJson))
                .andReturn();

        String content = saveResult.getResponse().getContentAsString();
        String newFileId = FileIdParser.getIdFromCreateFileResponse(content);

        MvcResult getByIdResult = this.mvc.perform(get("/file/" .concat(newFileId)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.tags", Matchers.empty()))
                .andReturn();
    }
}