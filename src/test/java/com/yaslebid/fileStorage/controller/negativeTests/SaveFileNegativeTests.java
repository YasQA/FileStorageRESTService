package com.yaslebid.fileStorage.controller.negativeTests;

import com.yaslebid.fileStorage.TestConfigAndData.TestData;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class SaveFileNegativeTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void saveWithEmptyFileName() throws Exception  {
        MvcResult result = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.emptyFileNameJson))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn();

        String content = result.getResponse().getContentAsString();
        Assertions.assertEquals(TestData.wrongFileNameResponse, content);
    }

    @Test
    void saveWithoutFileName() throws Exception  {
        MvcResult result = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.withoutFileNameJson))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn();

        String content = result.getResponse().getContentAsString();
        Assertions.assertEquals(TestData.wrongFileNameResponse, content);
    }

    @Test
    void saveWithNegativeFileSize() throws Exception  {
        MvcResult result = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.negativeFileSizeJson))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn();

        String content = result.getResponse().getContentAsString();
        Assertions.assertEquals(TestData.wrongFileSizeResponse, content);
    }

    @Test
    void saveWithoutFileSize() throws Exception  {
        MvcResult result = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.withoutFileSizeJson))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn();

        String content = result.getResponse().getContentAsString();
        Assertions.assertEquals(TestData.wrongFileSizeResponse, content);
    }
}
