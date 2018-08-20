package com.wf.wisdom_safety.model.inspect;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.wf.util.DirTraversal;
import com.wf.util.UserReadableException;
import com.wf.util.ZipUtils;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.bean.api.ApiResponse;
import com.wf.wisdom_safety.bean.api.ApiResponseRxFunc1;
import com.wf.wisdom_safety.bean.api.ListResponse;
import com.wf.wisdom_safety.bean.inspect.Building;
import com.wf.wisdom_safety.bean.inspect.Danger;
import com.wf.wisdom_safety.bean.inspect.NotDevice;
import com.wf.wisdom_safety.bean.inspect.OfflineDevice;
import com.wf.wisdom_safety.bean.inspect.Plain;
import com.wf.wisdom_safety.bean.inspect.PublicDevice;
import com.wf.wisdom_safety.data.network.ServerConfig;
import com.wf.wisdom_safety.data.network.WisdomSafetyApi;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 安全巡检主页数据管理
 * Created by Lionel on 2017/7/21.
 */

public class InspectManager {

    WisdomSafetyApi mWisdomSafetyApi;
    Context mContext;

    @Inject
    public InspectManager(Context context, WisdomSafetyApi wisdomSafetyApi) {
        mWisdomSafetyApi = wisdomSafetyApi;
        this.mContext = context;
    }

    /**
     * 获取巡检数据
     * @return
     */
    public Observable<Map<String, Object>> getInspectDatas() {
        return mWisdomSafetyApi.getInspectDatas().map(new Func1<ApiResponse<Map<String, Object>>, Map<String, Object>>() {
            @Override
            public Map<String, Object> call(ApiResponse<Map<String, Object>> mapApiResponse) {
                if(mapApiResponse != null && mapApiResponse.getReturnCode() == 0) {
                    return mapApiResponse.getResult();
                } else {
                    return null;
                }
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取巡检计划信息
     * @return
     */
    public Observable<List<Map<String, Object>>> getPlainList() {
        return mWisdomSafetyApi.getPlainExcute()
                .map(new ApiResponseRxFunc1<ApiResponse<ListResponse<Map<String, Object>>>, ListResponse<Map<String, Object>>>(mContext.getString(R.string.faild_to_get_data)))
                .map(new Func1<ListResponse<Map<String, Object>>, List<Map<String, Object>>>() {
                    @Override
                    public List<Map<String, Object>> call(ListResponse<Map<String, Object>> plainListResponse) {
                        return plainListResponse.getRows();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取建筑信息
     * @return
     */
    public Observable<List<Building>> getBuildingList() {
        return mWisdomSafetyApi.getBuildingList()
                .map(new ApiResponseRxFunc1<ApiResponse<ListResponse<Building>>, ListResponse<Building>>(mContext.getString(R.string.faild_to_get_data)))
                .map(new Func1<ListResponse<Building>, List<Building>>() {
                    @Override
                    public List<Building> call(ListResponse<Building> infoListResponse) {
                        return infoListResponse.getRows();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取安全隐患信息
     * @return
     */
    public Observable<List<Danger>> getDangerList() {
        return mWisdomSafetyApi.getDangerList()
                .map(new ApiResponseRxFunc1<ApiResponse<ListResponse<Danger>>, ListResponse<Danger>>(mContext.getString(R.string.faild_to_get_data)))
                .map(new Func1<ListResponse<Danger>, List<Danger>>() {
                    @Override
                    public List<Danger> call(ListResponse<Danger> infoListResponse) {
                        return infoListResponse.getRows();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取执行记录
     * @return
     */
    public Observable<List<Map<String, Object>>> getRecordList() {
        return mWisdomSafetyApi.getRecordList()
                .map(new ApiResponseRxFunc1<ApiResponse<ListResponse<Map<String, Object>>>, ListResponse<Map<String, Object>>>(mContext.getString(R.string.faild_to_get_data)))
                .map(new Func1<ListResponse<Map<String, Object>>, List<Map<String, Object>>>() {
                    @Override
                    public List<Map<String, Object>> call(ListResponse<Map<String, Object>> infoListResponse) {
                        return infoListResponse.getRows();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取非设备故障信息
     * @return
     */
    public Observable<List<NotDevice>> getNotDeviceList() {
        return mWisdomSafetyApi.getNotDeviceList()
                .map(new ApiResponseRxFunc1<ApiResponse<ListResponse<NotDevice>>, ListResponse<NotDevice>>(mContext.getString(R.string.faild_to_get_data)))
                .map(new Func1<ListResponse<NotDevice>, List<NotDevice>>() {
                    @Override
                    public List<NotDevice> call(ListResponse<NotDevice> infoListResponse) {
                        return infoListResponse.getRows();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取建筑位置信息
     * @return
     */
    public Observable<List<Map<String, Object>>> getBuildingsLocal() {
        return mWisdomSafetyApi.getBuildingsLocal()
                .map(new ApiResponseRxFunc1<ApiResponse<List<Map<String, Object>>>, List<Map<String, Object>>>(mContext.getString(R.string.faild_to_get_data)))
                .map(new Func1<List<Map<String, Object>>, List<Map<String, Object>>>() {
                    @Override
                    public List<Map<String, Object>> call(List<Map<String, Object>> maps) {
                        return maps;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取公共设备位置信息
     * @return
     */
    public Observable<List<PublicDevice>> getPubdevicesLocal() {
        return mWisdomSafetyApi.getPubdevicesLocal()
                .map(new ApiResponseRxFunc1<ApiResponse<ListResponse<PublicDevice>>, ListResponse<PublicDevice>>(mContext.getString(R.string.faild_to_get_data)))
                .map(new Func1<ListResponse<PublicDevice>, List<PublicDevice>>() {
                    @Override
                    public List<PublicDevice> call(ListResponse<PublicDevice> infoListResponse) {
                        return infoListResponse.getRows();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 根据计划获取设备数据
     * @param plainId
     * @return
     */
    public Observable<List<OfflineDevice>> getOffDevices(String plainId) {
        return mWisdomSafetyApi.getOffDevices(plainId)
                .map(new ApiResponseRxFunc1<ApiResponse<ListResponse<OfflineDevice>>, ListResponse<OfflineDevice>>(mContext.getString(R.string.faild_to_get_data)))
                .map(new Func1<ListResponse<OfflineDevice>, List<OfflineDevice>>() {
                    @Override
                    public List<OfflineDevice> call(ListResponse<OfflineDevice> infoListResponse) {
                        return infoListResponse.getRows();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 上传检查结果
     * @param excuteId
     * @param jsonFileStr
     * @param attachementFileStr
     * @return
     */
    public Observable<String> commitInspectResult(String excuteId, String jsonFileStr, String attachementFileStr)  {


        File jsonFile = new File(jsonFileStr);
        RequestBody jsonBody = RequestBody.create(MediaType.parse("application/octet-stream") , jsonFile);

         /* form的分割线,自己定义 */
        String boundary = "xx--------------------------------------------------------------xx";
        MultipartBody.Builder builder = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM)
                .addFormDataPart("excuteId" , excuteId)
                .addFormDataPart("attachements" , jsonFile.getName() , jsonBody);

        if(attachementFileStr != null) {
            File attachementFile = new File(attachementFileStr);
            RequestBody attachmentBody = RequestBody.create(MediaType.parse("application/octet-stream") , attachementFile);
            builder.addFormDataPart("attachements" , attachementFile.getName() , attachmentBody);
        }

        MultipartBody mBody = builder.build();

        return mWisdomSafetyApi.commitInspectResult(mBody)
                .map(new Func1<ApiResponse<String>, String>() {
                    @Override
                    public String call(ApiResponse<String> stringApiResponse) {
                        if(null != stringApiResponse && stringApiResponse.getReturnCode()==0) {
                            return stringApiResponse.getReturnCode()+"";
                        } else if(null != stringApiResponse && null != stringApiResponse.getDescription()) {
                            return stringApiResponse.getDescription();
                        } else {
                            throw new UserReadableException(mContext.getString(R.string.inspect_commit_fail));
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 上传安全隐患
     * @param excuteId
     * @param jsonFileStr
     * @param attachementFileStr
     * @return
     */
    public Observable<String> commitDangerResult(String excuteId, String jsonFileStr, String attachementFileStr)  {


        File jsonFile = new File(jsonFileStr);
        RequestBody jsonBody = RequestBody.create(MediaType.parse("application/octet-stream") , jsonFile);

         /* form的分割线,自己定义 */
        String boundary = "xx--------------------------------------------------------------xx";
        MultipartBody.Builder builder = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM)
                .addFormDataPart("excuteId" , excuteId)
                .addFormDataPart("attachements" , jsonFile.getName() , jsonBody);

        if(attachementFileStr != null) {
            File attachementFile = new File(attachementFileStr);
            RequestBody attachmentBody = RequestBody.create(MediaType.parse("application/octet-stream") , attachementFile);
            builder.addFormDataPart("attachements" , attachementFile.getName() , attachmentBody);
        }

        MultipartBody mBody = builder.build();

        return mWisdomSafetyApi.commitDangerResult(mBody)
                .map(new Func1<ApiResponse<String>, String>() {
                    @Override
                    public String call(ApiResponse<String> stringApiResponse) {
                        if(null != stringApiResponse && stringApiResponse.getReturnCode()==0) {
                            return stringApiResponse.getReturnCode()+"";
                        } else if(null != stringApiResponse && null != stringApiResponse.getDescription()) {
                            return stringApiResponse.getDescription();
                        } else {
                            throw new UserReadableException(mContext.getString(R.string.inspect_commit_fail));
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 上传非设备故障
     * @param excuteId
     * @param jsonFileStr
     * @param attachementFileStr
     * @return
     */
    public Observable<String> commitNotDeviceResult(String excuteId, String jsonFileStr, String attachementFileStr)  {


        File jsonFile = new File(jsonFileStr);
        RequestBody jsonBody = RequestBody.create(MediaType.parse("application/octet-stream") , jsonFile);

         /* form的分割线,自己定义 */
        String boundary = "xx--------------------------------------------------------------xx";
        MultipartBody.Builder builder = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM)
                .addFormDataPart("excuteId" , excuteId)
                .addFormDataPart("attachements" , jsonFile.getName() , jsonBody);

        if(attachementFileStr != null) {
            File attachementFile = new File(attachementFileStr);
            RequestBody attachmentBody = RequestBody.create(MediaType.parse("application/octet-stream") , attachementFile);
            builder.addFormDataPart("attachements" , attachementFile.getName() , attachmentBody);
        }

        MultipartBody mBody = builder.build();

        return mWisdomSafetyApi.commitNotDeviceResult(mBody)
                .map(new Func1<ApiResponse<String>, String>() {
                    @Override
                    public String call(ApiResponse<String> stringApiResponse) {
                        if(null != stringApiResponse && stringApiResponse.getReturnCode()==0) {
                            return stringApiResponse.getReturnCode()+"";
                        } else if(null != stringApiResponse && null != stringApiResponse.getDescription()) {
                            return stringApiResponse.getDescription();
                        } else {
                            throw new UserReadableException(mContext.getString(R.string.inspect_commit_fail));
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
