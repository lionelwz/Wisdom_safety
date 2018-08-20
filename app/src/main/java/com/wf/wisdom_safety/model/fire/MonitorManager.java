package com.wf.wisdom_safety.model.fire;

import android.content.Context;

import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.bean.api.ApiResponse;
import com.wf.wisdom_safety.bean.api.ApiResponseRxFunc1;
import com.wf.wisdom_safety.bean.monitor.UnitDevice;
import com.wf.wisdom_safety.data.network.WisdomSafetyApi;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by Lionel on 2017/9/23.
 */

public class MonitorManager {

    WisdomSafetyApi mWisdomSafetyApi;
    Context mContext;

    @Inject
    public MonitorManager(Context context, WisdomSafetyApi wisdomSafetyApi) {
        mWisdomSafetyApi = wisdomSafetyApi;
        this.mContext = context;
    }

    /*public Observable<List<UnitDevice>> getDevicesLocal() {
        return mWisdomSafetyApi.getDevicesLocal()
                .map(new ApiResponseRxFunc1<ApiResponse<List<UnitDevice>>, List<UnitDevice>>(mContext.getString(R.string.faild_to_get_data)))
                .map(new Func1<List<UnitDevice>, List<UnitDevice>>() {
                    @Override
                    public List<UnitDevice> call(List<UnitDevice> devices) {
                        return devices;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }*/

    public Observable<List<Map<String, Object>>> getDevicesLocal() {
        return mWisdomSafetyApi.getDevicesLocal2()
                .map(new ApiResponseRxFunc1<ApiResponse<List<Map<String, Object>>>, List<Map<String, Object>>>(mContext.getString(R.string.faild_to_get_data)))
                .map(new Func1<List<Map<String, Object>>, List<Map<String, Object>>>() {
                    @Override
                    public List<Map<String, Object>> call(List<Map<String, Object>> devices) {
                        return devices;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

}
