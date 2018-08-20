package cn.uetec.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件管理工具
 * @author Wangdan
 * 
 */
public class FileUtils {

	/**
	 * 保存图片
	 * @param bitName 绝对路径
	 * @param mBitmap 位图
     * @return file
     */
	public static File saveBitmap(String bitName, Bitmap mBitmap){
		File f = new File(bitName);
		FileOutputStream fOut = null;
		try {
			f.createNewFile();
			fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 95, fOut);
			return f;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				fOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	//获得指定文件的byte数组
	public static byte[] getBytes(File file){
		byte[] buffer = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * 计算图片采样大小
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
	
	/**
	 * Decode Bitmap from resource.
	 * @param res 
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return Bitmap
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
	}
	
	/**
	 * Decode bitmap from SDCard.
	 * @param srcPath
	 * @param reqWidth
	 * @param reqHeight
	 * @return Bitmap
	 */
	public static Bitmap decodeSampledBitmapFromSDCard(String srcPath,
	        int reqWidth, int reqHeight) {
		
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(srcPath, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(srcPath, options);
	}

	public static String init() {
		if(isSDCardAvaliable()) {
			File externalResFile = new File(Environment
					.getExternalStorageDirectory().getAbsolutePath(), "cn.uetec.Weike");
			if (!externalResFile.exists())
				externalResFile.mkdir();
			String externalResFilePath = externalResFile.getAbsolutePath();

			File cacheFile = new File(externalResFilePath, "cache");
			if (!cacheFile.exists())
				cacheFile.mkdir();
			return cacheFile.getAbsolutePath();
		} else {
			return null;
		}
	}

//	public static String getAudioRecordFilePath(){
//		if(isSDCardAvaliable()){
//			File mFile = new File(Constants.AUDIO_DIRECTORY);
//			if(!mFile.exists()){
//				try {
//					mFile.mkdirs();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			return mFile.getAbsolutePath();
//		}
//		return null;
//	}


	public static boolean isSDCardAvaliable(){
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	public static String getPcmFilePath(String audioRecordFileName){
		return init() + "/" + audioRecordFileName + Constant.PCM_SUFFIX;
	}

	public static String getMp3FilePath(String audioRecordFileName){
		return init() + "/" + audioRecordFileName + Constant.MP3_SUFFIX;
	}

}
