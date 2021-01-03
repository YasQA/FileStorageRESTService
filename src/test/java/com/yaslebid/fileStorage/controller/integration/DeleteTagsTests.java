package com.yaslebid.fileStorage.controller.integration;

import com.google.gson.Gson;
import com.yaslebid.fileStorage.helpers.TestFileOperator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DeleteTagsTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    TestFileOperator testFileOperator;

    private String newFileId;

    @BeforeEach
    public void setup() throws Exception {
        newFileId = testFileOperator.mvcCreateNewFile();
        testFileOperator.mvcAddTagsToFile(newFileId, Arrays.asList("tag1", "tag2", "tag3"));
    }

    @AfterEach
    public void teardown() throws Exception {
        testFileOperator.mvcDeleteFile(newFileId);
    }

    @Test
    void deleteTags_AndCheckIfTagsNotAvailableAnyMore() throws Exception {
        String tagsToRemove = new Gson().toJson(Arrays.asList("tag1", "tag2"));

        MvcResult deleteTagsResult = this.mvc.perform(delete("/file/{newFileId}/tags", newFileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(tagsToRemove))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        MvcResult getByIdResult = this.mvc.perform(get("/file/{newFileId}", newFileId))
                .andExpect(jsonPath("$.tags", Matchers.contains("tag3")))
                .andExpect(jsonPath("$.tags", Matchers.hasSize(1)))
                .andReturn();
    }

    @Test
    void delete_NotExistentTag() throws Exception {
        String tagsToRemove = new Gson().toJson(Arrays.asList("tag4"));

        MvcResult deleteTagsResult = this.mvc.perform(delete("/file/{newFileId}/tags", newFileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(tagsToRemove))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("tag not found on file"))
                .andReturn();
    }

    @Test
    void delete_ExistentAndNotExistentTags() throws Exception {
        String tagsToRemove = new Gson().toJson(Arrays.asList("tag1", "tag4"));

        MvcResult deleteTagsResult = this.mvc.perform(delete("/file/{newFileId}/tags", newFileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(tagsToRemove))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("tag not found on file"))
                .andReturn();
    }
}
