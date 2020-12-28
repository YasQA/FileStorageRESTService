package com.yaslebid.fileStorage.controller;

import com.yaslebid.fileStorage.TestConfigAndData.TestData;
import com.yaslebid.fileStorage.helpers.FileIdParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class DeleteFileTests {

    @Autowired
    private MockMvc mvc;

    String newFileId;

    @BeforeEach
    public void setup() throws Exception {
        MvcResult saveResult = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.newFileJson))
                .andReturn();

        String content = saveResult.getResponse().getContentAsString();
        newFileId = FileIdParser.getIdFromCreateFileResponse(content);
    }

    @Test
    void deleteFile() throws Exception {
        MvcResult deleteByIdResult = this.mvc.perform(delete("/file/".concat(newFileId)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        MvcResult getByIdResult = this.mvc.perform(get("/file/".concat(newFileId)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
