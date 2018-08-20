package com.wf.wisdom_safety.ui.inspect;

import com.wf.wisdom_safety.ui.WisdomSafetyModule;
import com.wf.wisdom_safety.ui.inspect.building.BuildingMainActivity;
import com.wf.wisdom_safety.ui.inspect.danger.DangerMainActivity;
import com.wf.wisdom_safety.ui.inspect.notdevice.NotDeviceMainActivity;
import com.wf.wisdom_safety.ui.inspect.plain.DangerCommitActivity;
import com.wf.wisdom_safety.ui.inspect.plain.DeviceInspectActivity;
import com.wf.wisdom_safety.ui.inspect.plain.NotDeviceCommitActivity;
import com.wf.wisdom_safety.ui.inspect.plain.OffDevicesActivity;
import com.wf.wisdom_safety.ui.inspect.plain.PhotoExpolorActivity;
import com.wf.wisdom_safety.ui.inspect.plain.PlainMainActivity;
import com.wf.wisdom_safety.ui.inspect.plain.ScanActivity;
import com.wf.wisdom_safety.ui.inspect.record.ExcuteRecordActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Lionel on 2017/7/21.
 */
@Singleton
@Component(modules = WisdomSafetyModule.class)
public interface InspectComponent {

    void inject(InspectFragment inspectFragment);

    void inject(PlainMainActivity plainMainActivity);

    void inject(BuildingMainActivity buildingMainActivity);

    void inject(DangerMainActivity dangerMainActivity);

    void inject(NotDeviceMainActivity notDeviceMainActivity);

    void inject(OffDevicesActivity offDevicesActivity);

    void inject(ScanActivity scanActivity);

    void inject(DeviceInspectActivity deviceInspectActivity);

    void inject(PhotoExpolorActivity photoExpolorActivity);

    void inject(DangerCommitActivity dangerCommitActivity);

    void inject(NotDeviceCommitActivity notDeviceCommitActivity);

    void inject(ExcuteRecordActivity excuteRecordActivity);

}
