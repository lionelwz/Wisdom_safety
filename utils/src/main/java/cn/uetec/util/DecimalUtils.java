package cn.uetec.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 小数处理工具
 * Created by Micheal Wang on 2016/3/8.
 */
public class DecimalUtils {
    /**
     * 对double数据进行取精度, 保留2位小数, 精度取值方式 BigDecimal.ROUND_HALF_UP
     * @param value double数据
     * @return 精度计算后的数据
     */
    public static double round(double value) {
        return round(value, 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 对double数据进行取精度
     * @param value double数据
     * @param scale 精度位数(保留的小数位数)
     * @param roundingMode 精度取值方式
     * @return 精度计算后的数据
     */
    public static double round(double value, int scale, int roundingMode) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, roundingMode);
        double d = bd.doubleValue();
        bd = null;
        return d;
    }

    /**
     * 将double数据转化成格式字符串
     * @param value
     * @param pattern
     * @return
     */
    public static String DecimalToString(double value, String pattern) {
        DecimalFormat format = new DecimalFormat(pattern);
        return format.format(value);
    }
}
