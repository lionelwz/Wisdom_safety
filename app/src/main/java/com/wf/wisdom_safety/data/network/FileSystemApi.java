package com.wf.wisdom_safety.data.network;

import com.wf.wisdom_safety.bean.api.ApiResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by Junhua Lv on 2016-7-13
 * 文件系统API.
 */
public interface FileSystemApi {

    /**
     * 上传文件
     * @param userId 用户ID
     * @param type   文件类型： 1表示头像
     * @param filePart   文件二进制
     * @return 文件在服务器的地址
     */
    @Multipart
    @POST("FileUpload")
    Observable<ApiResponse<List<String>>> uploadFile(@Part MultipartBody.Part userId, @Part MultipartBody.Part type, @Part MultipartBody.Part filePart);
}
