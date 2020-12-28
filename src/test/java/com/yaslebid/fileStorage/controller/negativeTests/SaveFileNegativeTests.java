package com.yaslebid.fileStorage.controller.negativeTests;

import com.yaslebid.fileStorage.TestConfigAndData.TestData;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
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
        Assert.assertEquals(TestData.wrongFileNameResponse, content);
    }

    @Test
    void saveWithoutFileName() throws Exception  {
        MvcResult result = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.withoutFileNameJson))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn();

        String content = result.getResponse().getContentAsString();
        Assert.assertEquals(TestData.wrongFileNameResponse, content);
    }

    @Test
    void saveWithNegativeFileSize() throws Exception  {
        MvcResult result = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.negativeFileSizeJson))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn();

        String content = result.getResponse().getContentAsString();
        Assert.assertEquals(TestData.wrongFileSizeResponse, content);
    }

    @Test
    void saveWithoutFileSize() throws Exception  {
        MvcResult result = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.withoutFileSizeJson))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn();

        String content = result.getResponse().getContentAsString();
        Assert.assertEquals(TestData.wrongFileSizeResponse, content);
    }
}
