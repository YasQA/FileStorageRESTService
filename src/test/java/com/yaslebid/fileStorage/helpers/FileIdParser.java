package com.yaslebid.fileStorage.helpers;

import org.json.JSONException;
import org.json.JSONObject;

public class FileIdParser {
    public static String getIdFromCreateFileResponse(String responseContent) throws JSONException {
        JSONObject jsonObject = new JSONObject(responseContent);
        return jsonObject.getString("ID");
    }
}
