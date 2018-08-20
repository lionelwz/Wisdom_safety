package com.wf.wisdom_safety.data.network;

import com.wf.wisdom_safety.bean.api.ApiResponse;
import com.wf.wisdom_safety.bean.api.ListResponse;
import com.wf.wisdom_safety.bean.inspect.Building;
import com.wf.wisdom_safety.bean.inspect.Danger;
import com.wf.wisdom_safety.bean.inspect.NotDevice;
import com.wf.wisdom_safety.bean.inspect.OfflineDevice;
import com.wf.wisdom_safety.bean.inspect.Plain;
import com.wf.wisdom_safety.bean.inspect.PublicDevice;
import com.wf.wisdom_safety.bean.monitor.UnitDevice;
import com.wf.wisdom_safety.bean.user.User;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Lionel on 2017/7/17.
 */

public interface WisdomSafetyApi {

    /**
     * 登录
     * @param username  用户名
     * @param password  密码
     * @return 用户信息
     */
    @GET("center/inf4User/fakeLogin")
    Observable<ApiResponse<User>> login(@Query("username") String username, @Query("password") String password);

    /**
     * 退出登录
     * @return
     */
    @GET("center/inf4User/fakeLogout")
    Observable<ApiResponse<String>> logout();

    /**
     * 获取巡检首界面数据
     * @return
     */
    @GET("inf4Inspect/getInspectDatas")
    Observable<ApiResponse<Map<String, Object>>> getInspectDatas();

    /**
     * 获取可执行的计划
     * @return
     */
    @GET("inf4Plain/getExcuterPlain")
    Observable<ApiResponse<ListResponse<Plain>>> getPlainList();

    /**
     * 获取巡检计划
     * @return
     */
    @GET("inf4PlainExcute/getPlainExcute")
    Observable<ApiResponse<ListResponse<Map<String, Object>>>> getPlainExcute();

    /**
     * 获取建筑物信息
     * @return
     */
    @GET("inf4Building/getBuildingsInfo")
    Observable<ApiResponse<ListResponse<Building>>> getBuildingList();

    /**
     * 获取安全隐患信息
     * @return
     */
    @GET("inf4Danger/getDangers")
    Observable<ApiResponse<ListResponse<Danger>>> getDangerList();

    /**
     * 获取执行记录
     * @return
     */
    @GET("inf4ExcuteRecord/getExcuteRecords")
    Observable<ApiResponse<ListResponse<Map<String, Object>>>> getRecordList();

    /**
     * 获取非设备信息
     * @return
     */
    @GET("inf4NotDevice/getNotDevices")
    Observable<ApiResponse<ListResponse<NotDevice>>> getNotDeviceList();

    /**
     * 获取建筑物状态信息
     * @return
     */
    @GET("inf4Building/getBuildingsLocal")
    Observable<ApiResponse<List<Map<String, Object>>>> getBuildingsLocal();

    /**
     * 获取公共设备信息
     * @return
     */
    @GET("inf4Pubdevice/getPubdevices")
    Observable<ApiResponse<ListResponse<PublicDevice>>> getPubdevicesLocal();

    /**
     * 获取计划设备离线数据
     * @return
     */
    @GET("inf4Plain/getOffDevices")
    Observable<ApiResponse<ListResponse<OfflineDevice>>> getOffDevices(@Query("plainId") String plainId);

    /**
     * 上传巡检结果数据
     * @param inspectResult
     * @return
     */
    @POST("inf4PlainExcute/uploadExcuteResult")
    Observable<ApiResponse<String>> commitInspectResult(@Body MultipartBody inspectResult);

    /**
     * 上传安全隐患结果数据
     * @param inspectResult
     * @return
     */
    @POST("inf4Danger/uploadDangerInfo")
    Observable<ApiResponse<String>> commitDangerResult(@Body MultipartBody inspectResult);

    /**
     * 上传非设备故障结果数据
     * @param inspectResult
     * @return
     */
    @POST("inf4NotDevice/uploadNotDeviceInfo")
    Observable<ApiResponse<String>> commitNotDeviceResult(@Body MultipartBody inspectResult);

    /**
     * 获取监测设备数据
     * @return
     */
    @GET("inf4Fire/getDevicesLocal")
    Observable<ApiResponse<List<UnitDevice>>> getDevicesLocal();

    /**
     * 获取监测设备数据
     * @return
     */
    @GET("inf4Fire/getDevicesLocal2")
    Observable<ApiResponse<List<Map<String, Object>>>> getDevicesLocal2();

    /**
     * 修改密码
     * @param oldPassword    原密码
     * @param newPassword    新密码
     * @param rePassword     重复密码
     * @return 0表示成功
     */
    @FormUrlEncoded
    @POST("center/inf4User/updatePassword")
    Observable<ApiResponse<Integer>> changePassword(@Field("oldPassword") String oldPassword, @Field("newPassword") String newPassword,
                                                    @Field("rePassword") String rePassword);

}
