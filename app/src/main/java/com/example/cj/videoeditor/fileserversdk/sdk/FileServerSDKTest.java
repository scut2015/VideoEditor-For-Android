package com.example.cj.videoeditor.fileserversdk.sdk;

import com.example.cj.videoeditor.fileserversdk.data.FileDTO;
import com.example.cj.videoeditor.fileserversdk.data.ResultDO;
import com.example.cj.videoeditor.fileserversdk.setting.Setting;
import com.example.cj.videoeditor.fileserversdk.setting.Util;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

public class FileServerSDKTest {
    Gson gson = new Gson();
    FileServerSDK fileServerSDK = new FileServerSDK();
    String testId = "kelleszzz";
    String testAccessCode = "tom44123";
    String testFileName = "congratulations.txt";
    String testContent = "Congratulations, you passed the test!";

    public boolean runTest() throws IOException {
        boolean test1=test1_Insert();
        boolean test2=test2_Get();
        boolean test3=test3_Update();
        boolean test4=test4_Remove();
        return test1 && test2 && test3 && test4;
    }

    public boolean test1_Insert() throws IOException {
        InputStream inputStream = null;
        ResultDO<Response> resultDO = null;
        try {
            FileDTO fileDTO = new FileDTO();
            fileDTO.setId(testId);
            fileDTO.setAccess_code(testAccessCode);
            fileDTO.setFile_name(testFileName);
            fileDTO.setInputStream(Util.bytesToInputStream(testContent.getBytes(Setting.DEFAULT_CHARSET)));
            resultDO = fileServerSDK.insert(fileDTO);
            return (resultDO.getSuccess() || resultDO.getCode() == Setting.STATUS_FILE_ALREADY_EXISTS);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public boolean test2_Get() throws IOException {
        InputStream inputStream = null;
        try {
            ResultDO<FileDTO> resultDO = fileServerSDK.get(testId, testAccessCode);
            if (!(resultDO.getSuccess() && resultDO.getData() != null && resultDO.getData().getInputStream() != null)) return false;
            FileDTO fileDTO = resultDO.getData();
            inputStream = fileDTO.getInputStream();
            byte[] bytes = Util.inputStreamToBytes(fileDTO.getInputStream());
            String responseContent = new String(bytes, Setting.DEFAULT_CHARSET);
            return(testContent.equals(responseContent) && testFileName.equals(fileDTO.getFile_name()));
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public boolean test3_Update() throws IOException {
        InputStream isUpdate=null, isGet = null;
        ResultDO<Response> resultUpdate = null;
        ResultDO<FileDTO> resultGet = null;
        try {
            resultUpdate = fileServerSDK.update(testId, testAccessCode,
                    null, "hucci.txt", Util.bytesToInputStream("Hello, Hucci!".getBytes(Setting.DEFAULT_CHARSET)));
            if (!(resultUpdate != null && resultUpdate.getSuccess())) return false;
            resultGet = fileServerSDK.get(testId, testAccessCode);
            if (!(resultGet != null && resultGet.getSuccess() && resultGet.getData() != null)) return false;
            FileDTO fileDTO=resultGet.getData();
            return ("Hello, Hucci!".equals(new String(Util.inputStreamToBytes(fileDTO.getInputStream()), Setting.DEFAULT_CHARSET)));
        } finally {
            if (isUpdate != null) {
                isUpdate.close();
            }
            if (isGet != null) {
                isGet.close();
            }
        }
    }

    public boolean test4_Remove(){
        ResultDO resultDO=fileServerSDK.remove(testId,testAccessCode);
        return(resultDO.getSuccess());
    }

}
