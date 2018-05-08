package com.example.cj.videoeditor.fileserversdk.sdk;

import com.example.cj.videoeditor.fileserversdk.data.FileDTO;
import com.example.cj.videoeditor.fileserversdk.data.ResultDO;
import com.example.cj.videoeditor.fileserversdk.okhttp.InputStreamRequestBody;
import com.example.cj.videoeditor.fileserversdk.setting.Setting;
import com.example.cj.videoeditor.fileserversdk.setting.Util;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileServerSDK implements Closeable {

    protected OkHttpClient client = new OkHttpClient();
    protected Gson gson = new Gson();
    final static Pattern patternFileName = Pattern.compile("attachment; filename=\"(.+)\"");

    public ResultDO remove(String id, String access_code) {
        if (Util.isEmpty(id) || Util.isEmpty(access_code)) {
            return Util.getResultDO(false, Setting.STATUS_INVALID_PARAMETER, Setting.MESSAGE_INVALID_PARAMETER);
        }
        FileDTO fileDTO = null;
        Response response = null;
        ResultDO resultDO = null;
        try {
            RequestBody requestBody = new FormBody.Builder().add("id", id).add("access_code", access_code).build();
            Request request = new Request.Builder().url(Setting.URL_BASIC + Setting.URL_REMOVE).post(requestBody).build();
            response = client.newCall(request).execute();
            resultDO = responseToResultDO(response);
            resultDO.setData(response);

            if (resultDO.getSuccess()) {
                Util.log("Remove fileDTO = %s, \nresult = %s", gson.toJson(Util.fileDTOInfo(fileDTO)), gson.toJson(Util.resultDOInfo(resultDO)));
                return resultDO;
            }
            Util.log("Remove Error, fileDTO = %s, response_code = %s\nresult = %s", gson.toJson(Util.fileDTOInfo(fileDTO)),
                    response == null ? null : response.code(), gson.toJson(Util.resultDOInfo(resultDO)));
            return Util.getResultDO(false, Setting.STATUS_ERROR, "Response Error, code = " + (response == null ? null : response.code()));
        } catch (IOException e) {
            e.printStackTrace();
            Util.log("Remove Error, id = %s, access_code = %s\nresult = %s", id, access_code, gson.toJson(Util.resultDOInfo(resultDO)));
            return Util.getResultDO(false, Setting.STATUS_ERROR);
        } finally {
            if (response != null) response.close();
        }
    }

    /**
     * 获取displayFile链接
     *
     * @param id
     * @param access_code
     * @return
     */
    public String getDisplay(String id, String access_code) {
        HttpUrl httpUrl = HttpUrl.parse(Setting.URL_BASIC + Setting.PATH_UI + Setting.PATH_DISPLAY).newBuilder()
                .addQueryParameter("id", id)
                .addQueryParameter("access_code", access_code)
                .build();
        return httpUrl.toString();
    }

    /**
     * 不要忘记关闭FileDTO的InputStream
     *
     * @param id
     * @param access_code
     * @return
     */
    public ResultDO<FileDTO> get(String id, String access_code) {
        if (Util.isEmpty(id) || Util.isEmpty(access_code)) {
            return Util.<FileDTO>getResultDO(false, Setting.STATUS_INVALID_PARAMETER, Setting.MESSAGE_INVALID_PARAMETER);
        }
        FileDTO fileDTO = null;
        ResultDO<FileDTO> resultDO = null;
        Response response = null;
        try {
            HttpUrl httpUrl = HttpUrl.parse(Setting.URL_BASIC + Setting.URL_GET).newBuilder()
                    .addQueryParameter("id", id)
                    .addQueryParameter("access_code", access_code)
                    .build();
            Request request = new Request.Builder().url(httpUrl).build();
            response = client.newCall(request).execute();
            if (response != null && response.isSuccessful() && response.body() != null) {
                //从Header中获取FileDTO信息
                String json = response.header(Setting.HEADER_FILEDTO_INFO, null);
                fileDTO = Util.jsonToFileDTO(json, gson);
                if (fileDTO == null) {
                    Util.log("Get Error, json = %s, id = %s, access_code = %s", json, id, access_code);
                    return Util.getResultDO(false, Setting.STATUS_PARSE_JSON_ERROR, Setting.MESSAGE_PARSE_JSON_ERROR);
                }
                if (!id.equals(fileDTO.getId())) {
                    //获取id不一致
                    Util.log("Get Error, accessId = %s, id = %s, access_code = %s", fileDTO.getId(), id, access_code);
                    return Util.getResultDO(false, Setting.STATUS_ACCESS_DENIED, Setting.MESSAGE_ACCESS_DENIED);
                }
                fileDTO.setInputStream(response.body().byteStream());
                resultDO = Util.getResultDO(true, null, null, fileDTO);
                Util.log("Get fileDTO = %s, \nresult = %s", gson.toJson(Util.fileDTOInfo(fileDTO)), gson.toJson(Util.resultDOInfo(resultDO)));
                return resultDO;
            }
            Util.log("Get Error, id = %s, access_code = %s, response_code = %s\nresult = %s", id, access_code,
                    response == null ? null : response.code(), gson.toJson(Util.resultDOInfo(resultDO)));
            return Util.getResultDO(false, Setting.STATUS_ERROR, "Response Error, code = " + (response == null ? null : response.code()));
        } catch (IOException e) {
            e.printStackTrace();
            Util.log("Get Error, id = %s, access_code = %s, \nresult = %s", id, access_code, gson.toJson(Util.resultDOInfo(resultDO)));
            return Util.getResultDO(false, Setting.STATUS_ERROR);
        } finally {
//            if (response != null) response.close();
        }
    }

    /**
     * 不要忘记关闭InputStream
     *
     * @param id
     * @param access_code
     * @param file_name
     * @param inputStream
     * @return
     */
    public ResultDO insert(String id, String access_code, String file_name, InputStream inputStream) {
        if (Util.isEmpty(id) || Util.isEmpty(access_code) || Util.isEmpty(file_name) || inputStream == null) {
            return Util.getResultDO(false, Setting.STATUS_INVALID_PARAMETER, Setting.MESSAGE_INVALID_PARAMETER);
        }
        FileDTO fileDTO = new FileDTO();
        fileDTO.setId(id);
        fileDTO.setAccess_code(access_code);
        fileDTO.setFile_name(file_name);
        fileDTO.setInputStream(inputStream);
        return insert(fileDTO);
    }

    public ResultDO update(String id, String access_code, String new_access_code, String file_name, InputStream inputStream) {
        if (Util.isEmpty(id) || Util.isEmpty(access_code)) {
            return Util.getResultDO(false, Setting.STATUS_INVALID_PARAMETER, Setting.MESSAGE_INVALID_PARAMETER);
        }
        UpdateFileDTO updateFileDTO = new UpdateFileDTO();
        updateFileDTO.setId(id);
        updateFileDTO.setAccess_code(access_code);
        updateFileDTO.setNew_access_code(new_access_code);
        updateFileDTO.setFile_name(file_name);
        updateFileDTO.setInputStream(inputStream);
        return update(updateFileDTO);
    }

    protected ResultDO update(UpdateFileDTO fileDTO) {
        if (fileDTO == null || Util.isEmpty(fileDTO.getId()) || Util.isEmpty(fileDTO.getAccess_code()) || Util.isEmpty(fileDTO.getFile_name())) {
            return Util.getResultDO(false, Setting.STATUS_INVALID_PARAMETER, Setting.MESSAGE_INVALID_PARAMETER);
        }
        Response response = null;
        ResultDO resultDO = null;
        try {
            MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", fileDTO.getId())
                    .addFormDataPart("access_code", fileDTO.getAccess_code());
            if (!Util.isEmpty(fileDTO.getNew_access_code()))
                multipartBodyBuilder.addFormDataPart("new_access_code", fileDTO.getNew_access_code());
            if (!Util.isEmpty(fileDTO.getFile_name()))
                multipartBodyBuilder.addFormDataPart("file_name", fileDTO.getFile_name());
            if (!Util.isEmpty(fileDTO.getInputStream())) {
                multipartBodyBuilder.addPart(MultipartBody.Part.createFormData("file", fileDTO.getFile_name() != null ? fileDTO.getFile_name() : Setting.TYPE_NOT_SPECIFIED,
                        new InputStreamRequestBody(MediaType.parse("application/octet-stream"), fileDTO.getInputStream())));
            }
            Request request = new Request.Builder()
                    .url(Setting.URL_BASIC + Setting.URL_UPDATE)
                    .post(multipartBodyBuilder.build())
                    .build();
            response = client.newCall(request).execute();
            resultDO = responseToResultDO(response);
            resultDO.setData(response);
            if (resultDO.getSuccess()){
                Util.log("Update fileDTO = %s, \nresult = %s", gson.toJson(Util.fileDTOInfo(fileDTO)), gson.toJson(Util.resultDOInfo(resultDO)));
                return resultDO;
            }
            Util.log("Update Error, fileDTO = %s, response_code = %s\nresult = %s", gson.toJson(Util.fileDTOInfo(fileDTO)),
                    response == null ? null : response.code(), gson.toJson(Util.resultDOInfo(resultDO)));
            return resultDO;
        } catch (IOException e) {
            e.printStackTrace();
            Util.log("Update Error, fileDTO = %s, \nresult = %s", gson.toJson(Util.fileDTOInfo(fileDTO)), gson.toJson(Util.resultDOInfo(resultDO)));
            return Util.getResultDO(false, Setting.STATUS_ERROR);
        } finally {
            if (response != null) response.close();
        }
    }

    @SuppressWarnings("unchecked")
    protected ResultDO insert(FileDTO fileDTO) {
        if (fileDTO == null || Util.isEmpty(fileDTO.getId()) || Util.isEmpty(fileDTO.getAccess_code()) || Util.isEmpty(fileDTO.getFile_name())) {
            return Util.getResultDO(false, Setting.STATUS_INVALID_PARAMETER, Setting.MESSAGE_INVALID_PARAMETER);
        }
        Response response = null;
        ResultDO resultDO = null;
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", fileDTO.getId())
                    .addFormDataPart("access_code", fileDTO.getAccess_code())
                    .addFormDataPart("file_name", fileDTO.getFile_name())
                    .addPart(MultipartBody.Part.createFormData("file", fileDTO.getFile_name(),
                            new InputStreamRequestBody(MediaType.parse("application/octet-stream"), fileDTO.getInputStream())))
                    .build();
            Request request = new Request.Builder()
                    .url(Setting.URL_BASIC + Setting.URL_INSERT)
                    .post(requestBody)
                    .build();
            response = client.newCall(request).execute();
            resultDO = responseToResultDO(response);
            resultDO.setData(response);
            if (resultDO.getSuccess()) {
                Util.log("Insert fileDTO = %s, \nresult = %s", gson.toJson(Util.fileDTOInfo(fileDTO)), gson.toJson(Util.resultDOInfo(resultDO)));
                return resultDO;
            }
            Util.log("Insert Error, fileDTO = %s, response_code = %s\nresult = %s", gson.toJson(Util.fileDTOInfo(fileDTO)),
                    response == null ? null : response.code(), gson.toJson(Util.resultDOInfo(resultDO)));
            return Util.getResultDO(false, Setting.STATUS_ERROR, "Response Error, code = " + (response == null ? null : response.code()));
        } catch (IOException e) {
            e.printStackTrace();
            Util.log("Insert Error, fileDTO = %s, \nresult = %s", gson.toJson(Util.fileDTOInfo(fileDTO)), gson.toJson(Util.resultDOInfo(resultDO)));
            return Util.getResultDO(false, Setting.STATUS_ERROR);
        } finally {
            if (response != null) response.close();
        }
    }

    /**
     * 从ResponseBody中提取出json,转换为ResultDO
     *
     * @param response
     * @return 始终不为null
     */
    protected ResultDO responseToResultDO(Response response) {
        ResultDO resultDO = null;
        try {
            if (response != null && response.isSuccessful() && response.body() != null) {
                String responseBody = new String(response.body().bytes(), Setting.DEFAULT_CHARSET);
                try {
                    resultDO = gson.fromJson(responseBody, ResultDO.class);
                } catch (JsonSyntaxException e) {
                    Util.log("ResponseToResultDO Error, json = %s", responseBody);
                    resultDO = Util.getResultDO(false, Setting.STATUS_PARSE_JSON_ERROR, Setting.MESSAGE_PARSE_JSON_ERROR);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (resultDO == null) {
            Util.log("ResponseToResultDO Error, code = %s", response == null ? "null" : String.valueOf(response.code()));
            resultDO = Util.getResultDO(false, Setting.STATUS_RESPONSE_FAILURE, Setting.MESSAGE_RESPONSE_FAILURE);
        }
        if (resultDO==null) throw new NullPointerException("responseToResultDO is NULL!");
        return resultDO;
    }

    @Override
    public void close() throws IOException {

    }

    protected static class UpdateFileDTO extends FileDTO {
        String new_access_code;

        public String getNew_access_code() {
            return new_access_code;
        }

        public void setNew_access_code(String new_access_code) {
            this.new_access_code = new_access_code;
        }
    }

}
