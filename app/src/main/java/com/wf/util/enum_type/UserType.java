package com.wf.util.enum_type;

/**
 * Created by Lionel on 2017/9/20.
 */

public enum UserType {

    ADMIN((Integer)1), ORGANIZATION_ADMIN((Integer)2), MAINTENANCE_ADMIN((Integer)3), UNIT_ADMIN((Integer)4),
    GENERAL_USER((Integer)5),ORG_GENERAL_USER((Integer)6);

    private final Integer value;

    private UserType(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}
