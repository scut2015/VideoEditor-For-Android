package com.example.cj.videoeditor.fileserversdk.setting;

import android.util.Log;

import com.example.cj.videoeditor.fileserversdk.data.FileDTO;
import com.example.cj.videoeditor.fileserversdk.data.ResultDO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Util {

    /**
     * 从InputStream读取字节,写入byte数组
     * InputStream中的数据不能过大
     *
     * @param inputStream
     * @return
     */
    public static byte[] inputStreamToBytes(InputStream inputStream) {
        if (inputStream == null) return null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (byte[] bytes = new byte[1024]; ; ) {
                int bytesRead = inputStream.read(bytes);
                if (bytesRead == -1) break;
                baos.write(bytes, 0, bytesRead);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static InputStream stringToInputStream(String msg) {
        if (msg == null) return null;
        return bytesToInputStream(msg.getBytes(Setting.DEFAULT_CHARSET));
    }

    public static InputStream bytesToInputStream(byte[] bytes) {
        if (bytes == null) return null;
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return bais;
    }

    public static FileDTO jsonToFileDTO(String json, Gson gson) {
        if (json == null || gson == null) return null;
        try {
            FileDTO fileDTO = gson.fromJson(json, FileDTO.class);
            return fileDTO;
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public static <T> ResultDO<T> getResultDO(boolean success, Integer code, String message, T data) {
        ResultDO<T> resultDO = new ResultDO<T>();
        resultDO.setSuccess(success);
        resultDO.setCode(code);
        resultDO.setMessage(message);
        resultDO.setData(data);
        return resultDO;
    }

    public static <T> ResultDO<T> getResultDO(boolean success, int code, String message) {
        return getResultDO(success, code, message, null);
    }

    public static <T> ResultDO<T> getResultDO(boolean success, String message) {
        return getResultDO(success, null, message, null);
    }

    public static <T> ResultDO<T> getResultDO(boolean success, int code) {
        return getResultDO(success, code, null, null);
    }

    public static <T> ResultDO<T> getResultDO(boolean success) {
        return getResultDO(success, null, null, null);
    }

    public static FileDTO fileDTOInfo(FileDTO fileDTO) {
        if (fileDTO == null) return null;
        FileDTO fileDTOInfo = new FileDTO();
        Util.copyFileDTO(fileDTOInfo, fileDTO);
        fileDTOInfo.setInputStream(null);
        return fileDTOInfo;
    }

    public static void copyFileDTO(FileDTO dest, FileDTO orig) {
        if (dest == null || orig == null) return;
        dest.setAccess_code(orig.getAccess_code());
        dest.setCreate_time(orig.getCreate_time());
        dest.setFile_name(orig.getFile_name());
        dest.setId(orig.getId());
        dest.setInputStream(orig.getInputStream());
        dest.setSize(orig.getSize());
    }

    public static void copyResultDO(ResultDO dest, ResultDO orig) {
        if (dest == null || orig == null) return;
        dest.setCode(orig.getCode());
        dest.setData(orig.getData());
        dest.setMessage(orig.getMessage());
        dest.setSuccess(orig.getSuccess());
    }

    public static <T> ResultDO<T> resultDOInfo(ResultDO resultDO) {
        if (resultDO == null) return null;
        ResultDO<T> resultDOInfo = new ResultDO<>();
        Util.copyResultDO(resultDOInfo, resultDO);
        resultDOInfo.setData(null);
        return resultDOInfo;
    }

    public static void log(String format, Object... args) {
        if (format == null) return;
        Log.d("kelles", String.format(format, args));
    }

    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }
}