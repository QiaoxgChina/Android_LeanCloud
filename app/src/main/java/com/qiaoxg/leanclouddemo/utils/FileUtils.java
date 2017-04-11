package com.qiaoxg.leanclouddemo.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by admin on 2017/3/14.
 */

public class FileUtils {

    /**
     * 获取手机内置存储路径
     * @return
     */
    public static String getExternalStorageDirectory(){
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 根据路径判断文件是否存在
     * @param path
     * @return
     */
    public static boolean isFileExistByPath(String path){
        if (TextUtils.isEmpty(path)){
            return false;
        }
        File f = new File(path);
        if (f.exists() && f.isFile()){
            return true;
        }
        return false;
    }

    /**
     * 获得本地保存声音的路径
     * @return
     */
    public static String getLocalAudioDir() {
        String result = null;
        File videoDirFile = null;
        boolean error = false;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            result = getExternalStorageDirectory();
            result += File.separator + "audioMsgTemp/";
            videoDirFile = new File(result);
            if (!videoDirFile.exists() && !videoDirFile.mkdirs()) {
                error = true;
            }
        }
        if (error)
            return null;
        return result;
    }

}
