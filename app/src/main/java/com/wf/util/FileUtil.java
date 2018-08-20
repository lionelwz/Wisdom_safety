package com.wf.util;

import android.content.Context;

import com.wf.wisdom_safety.ui.WisdomSafetyModule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * 数据存储和获取
 * Created by Lionel on 2017/7/24.
 */

public class FileUtil {

    public static String loadFile(String fileName) {
        BufferedReader reader = null;
        try {
            InputStream in = WisdomSafetyModule.getContext().openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            return stringBuilder.toString();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //根据文件获取文件内容
    public static String loadFile(File file) {
        BufferedReader reader = null;
        try {
            InputStream in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            return stringBuilder.toString();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean saveFile(String fileName, String infos) {
        FileOutputStream outputStream;
        try {
            File file = new File(WisdomSafetyModule.getContext().getFilesDir(), fileName);
            outputStream = WisdomSafetyModule.getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(infos.getBytes());
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean saveFile(File file, String infos) {
        FileOutputStream outputStream;
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file);
            outputStream.write(infos.getBytes());
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void deleteAllFilesOfDir(File path) {
        if (!path.exists())
            return;
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteAllFilesOfDir(files[i]);
        }
        path.delete();
    }


}
