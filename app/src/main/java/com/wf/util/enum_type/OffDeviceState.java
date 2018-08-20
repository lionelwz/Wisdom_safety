package com.wf.util.enum_type;

/**
 * 巡检设备状态
 * Created by Lionel on 2017/7/26.
 */

public enum OffDeviceState {

    NORMAL((Integer)1), FAULT((Integer)2), BREAKDOWN_LIGHT((Integer)3), BREAKDOWN_MIDDLE((Integer)4), BREAKDOWN_SERIOUS((Integer)5);

    private final Integer value;

    private OffDeviceState(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}
