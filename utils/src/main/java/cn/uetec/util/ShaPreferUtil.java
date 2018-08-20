package cn.uetec.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具类.
 * @author Wangdan
 *
 */
public class ShaPreferUtil {
	public static String PREFERENCE_NAME = "VersionUpdate";

	public static String BUYER_NAME = "buyer_name";
	public static String BUYER_ADDRESS = "buyer_address";


	private SharedPreferences shpref;

	/**
	 * 创建这样的SharedPreferences文件 通过构造方法直接创建
	 */
	public ShaPreferUtil(Context context, String name) {
		// MODE_WORLD_READABLE,MODE_WORLD_WRITEABLE不鼓励使用，去做数据共享
		shpref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	/**
	 * SharedPreferences文件保存字符串
	 * @param key
	 * @param value
	 */
	public void write(String key, String value) {
		// 获取编辑器
		SharedPreferences.Editor editor = shpref.edit();
		// 写入内容
		editor.putString(key, value);
		// 提交
		editor.commit();
	}

	// 方法的重载(是方法的重载)
	// 方法的重载，名字一致，参数列表不同，构成重载(不看返回值的)
	/**
	 * SharedPreferences文件保存整型数
	 * @param key
	 * @param value
	 */
	public void write(String key, int value) {
		// 获取编辑器
		SharedPreferences.Editor editor = shpref.edit();
		// 写入内容
		editor.putInt(key, value);
		// 提交
		editor.commit();
	}

	/**
	 * SharedPreferences文件保存布尔类型值
	 * @param key
	 * @param value
	 */
	public void write(String key, boolean value) {
		// 获取编辑器
		SharedPreferences.Editor editor = shpref.edit();
		// 写入内容
		editor.putBoolean(key, value);
		// 提交
		editor.commit();
	}

	/**
	 * SharedPreferences文件的读取内容
	 * @param key
	 * @param defValue
	 * @return String
	 */
	public String read(String key, String defValue) {
		return shpref.getString(key, defValue);
	}

	/**
	 * SharedPreferences文件的读取内容
	 * @param key
	 * @param defValue
	 * @return int
	 */
	public int read(String key, int defValue) {
		return shpref.getInt(key, defValue);
	}

	/**
	 * SharedPreferences文件的读取内容
	 * @param key
	 * @param defValue
	 * @return boolean
	 */
	public boolean read(String key, boolean defValue) {
		return shpref.getBoolean(key, defValue);
	}

	/**
	 * put long preferences
	 *
	 * @param key The name of the preference to modify
	 * @param value The new value for the preference
	 * @return True if the new values were successfully written to persistent storage.
	 */
	public void write(String key, long value) {
		// 获取编辑器
		SharedPreferences.Editor editor = shpref.edit();
		// 写入内容
		editor.putLong(key, value);
		// 提交
		editor.commit();
	}

	/**
	 * get long preferences
	 *
	 * @param key The name of the preference to retrieve
	 * @param defaultValue Value to return if this preference does not exist
	 * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
	 *         this name that is not a long
	 */
	public long read(String key, long defaultValue) {
		return shpref.getLong(key, defaultValue);
	}

	/**
	 * 根据指定的key,删除这组key-value
	 */
	public void removeByKey(String key) {
		// 获取编辑器
		SharedPreferences.Editor editor = shpref.edit();
		// 根据key,删除这组key-value
		editor.remove(key);
		// 提交
		editor.commit();
	}

	/**
	 * 清除文件里所有key-value
	 */
	public void removeAll() {
		// 获取编辑器
		SharedPreferences.Editor editor = shpref.edit();
		// 根据key,删除这组key-value
		editor.clear();
		// 提交
		editor.commit();
	}
}
