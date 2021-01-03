package com.yaslebid.fileStorage.controller.integration;

import com.google.gson.Gson;
import com.yaslebid.fileStorage.helpers.TestFileOperator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AddTagsTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private TestFileOperator testFileOperator;

    private String newFileId;

    @BeforeEach
    public void setup() throws Exception {
        newFileId = testFileOperator.mvcCreateNewFile();
    }

    @AfterEach
    public void teardown() throws Exception {
        testFileOperator.mvcDeleteFile(newFileId);
    }

    @Test
    void addTagsToFileBasicTest() throws Exception {
        String tagsToAdd = new Gson().toJson(Arrays.asList("tag1", "tag2"));

        MvcResult result = this.mvc.perform(post("/file/{newFileID}/tags", newFileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(tagsToAdd))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();
    }

    @Test
    void addTagsToFile_andCheckIfTagsAddedTest() throws Exception {
        String tagsToAdd = new Gson().toJson(Arrays.asList("tag1", "tag2"));
        this.mvc.perform(post("/file/{newFileID}/tags", newFileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(tagsToAdd));

        MvcResult getByIdResult = this.mvc.perform(get("/file/{newFileId}", newFileId))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.tags", Matchers.contains("tag1", "tag2")))
                .andReturn();
    }

    @Test
    void addTheSameTagsToFileTwice_andCheckIfTAgsNotDuplicated() throws Exception {

        List<String> tags = Arrays.asList("tag1", "tag2");

        testFileOperator.mvcAddTagsToFile(newFileId, tags);
        testFileOperator.mvcAddTagsToFile(newFileId, tags);

        MvcResult getByIdResult = this.mvc.perform(get("/file/{newFileId}", newFileId))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.tags", Matchers.contains("tag1", "tag2")))
                .andExpect(jsonPath("$.tags", Matchers.hasSize(2)))
                .andReturn();
    }

    @Test
    void addIntersectionTagsToFile_andCheckCorrectMergeTest() throws Exception {

        List<String> tags1 = Arrays.asList("tag1", "tag2");
        List<String> tags2 = Arrays.asList("tag2", "tag3");

        testFileOperator.mvcAddTagsToFile(newFileId, tags1);
        testFileOperator.mvcAddTagsToFile(newFileId, tags2);

        MvcResult getByIdResult = this.mvc.perform(get("/file/{newFileId}", newFileId))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.tags", Matchers.contains("tag1", "tag2", "tag3")))
                .andExpect(jsonPath("$.tags", Matchers.hasSize(3)))
                .andReturn();
    }

}
