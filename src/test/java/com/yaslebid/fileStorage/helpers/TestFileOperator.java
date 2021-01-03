package com.yaslebid.fileStorage.helpers;

import com.google.gson.Gson;
import com.yaslebid.fileStorage.TestConfigAndData.TestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestFileOperator {
    @Autowired
    private MockMvc mvc;

    public String mvcCreateNewFile() throws Exception {
        MvcResult saveResult = this.mvc.perform(post("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.NEW_FILE_UNKNOWN_TYPE_JSON))
                .andReturn();

        String content = saveResult.getResponse().getContentAsString();
        return FileIdParser.getIdFromCreateFileResponse(content);
    }

    public void mvcDeleteFile(String fileId) throws Exception {
        this.mvc.perform(delete("/file/{newFileId}", fileId));
    }

    public void mvcAddTagsToFile(String fileId, List<String> tags) throws Exception {
        String stringTags = new Gson().toJson(tags);

        this.mvc.perform(post("/file/{fileId}/tags", fileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(stringTags));
    }


}
