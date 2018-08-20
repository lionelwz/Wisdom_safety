package cn.uetec.util;

/**
 * 常量类
 * Created by Micheal Wang on 2016/11/16.
 */

public class Constant {
    public static final int IM_TIMEOUT = 1000 * 8;
    public static final int IM_RE_CONNET_TIME = 5;
    public static final int BUTTON_ENABLE_TIME = 1000 * 5;
    public static final int TOOLBAR_HIDE_TIME = 1000 * 5;
    public static final int REQUEST_CODE_EVALUATE = 12;
    public static final int LIVE_COURSE = 1;
    public static final int EXCELLENT_COURSE = 2;
    /** 收藏类型，收藏 */
    public static final int TYPE_COLLECT = 1;
    /** 收藏类型，取消收藏 */
    public static final int TYPE_COLLECTED_CANCEL = 2;

    public static final String ACTION_LOGIN_ACTIVITY = "cn.uetec.weike.ui.user.LoginActivity";
    public static final String ACTION_EVALUATE_ACTIVITY= "cn.uetec.classonlinestudent.ui.course.EvaluateActivity";
    public static final String ACTION_EVALUATE_ADD_ACTIVITY= "cn.uetec.classonlinestudent.ui.course.EvaluateAddActivity";
    public static final String PRE_ORDER = "order";
    public static final String PRE_ORDER_ID = "order_id";
    public static final String EXTRA_COURSE_DETAILS = "course_details";
    public static final String EXTRA_COURSE_TYPE = "courseType";
    public static final String EXTRA_EVALUATE_DETAILS = "evaluate_details";
    /**
     * 录制音频初始后缀
     */
    public static final String PCM_SUFFIX = ".pcm";

    /**
     * 转换成aac音频后缀
     */
    //public static final String AAC_SUFFIX = ".aac";

    /**
     * 转换成m4a音频后缀
     */
    //public static final String M4A_SUFFIX = ".m4a";

    /**
     * 转换成mp3音频后缀
     */
    public static final String MP3_SUFFIX = ".mp3";

}
