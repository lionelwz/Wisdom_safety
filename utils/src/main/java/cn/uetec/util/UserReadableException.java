package cn.uetec.util;

/**
 * Created by Junhua Lv on 2016/7/21.
 * 具有用户可读性的exception， message可以直接toast出来
 */
public class UserReadableException extends RuntimeException {
    public UserReadableException(String detailMessage) {
        super(detailMessage);
    }
}
