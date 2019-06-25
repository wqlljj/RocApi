package com.cloudminds.rocapi.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Arche on 2016/11/23.
 */

public class FileUtil {

    public static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static String readLog(){
        String fileName = mSimpleDateFormat.format(new Date()).split(" ")[0] + "_DeviceTest.log";
        String filePath = "/sdcard/" + fileName;
        return readFile(filePath);
    }

    public static void writeLog(String content){
        String time = mSimpleDateFormat.format(new Date());
        String fileName = time.split(" ")[0]  + "_DeviceTest.log";
        String filePath = "/sdcard/" + fileName;
        writeFile(filePath,"\n");
        writeFile(filePath,"    [ - ] " + content + "\n");
        writeFile(filePath,time + ":\n");
    }

    public static void recordCameraDisplayOrientation(String content){
        String fileName = mSimpleDateFormat.format(new Date()).split(" ")[0] + "_CameraDisplayOrientation.log";
        String filePath = "/sdcard/" + fileName;
        writeFile(filePath,content);
    }

    public static String readFile(String filePath) {
        StringBuffer sb = new StringBuffer();
        File file = new File(filePath);
        try {
            FileInputStream fin = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fin);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void writeFile(String filePath, String content) {
        try{
            File tmp = File.createTempFile("tmp",null);
            tmp.deleteOnExit();
            RandomAccessFile raf = new RandomAccessFile(filePath,"rw");
            FileOutputStream tmpOut = new FileOutputStream(tmp);
            FileInputStream tmpIn = new FileInputStream(tmp);
            raf.seek(0);//首先的话是0
            byte[] buf = new byte[64];
            int hasRead = 0;
            while((hasRead = raf.read(buf))>0){
                tmpOut.write(buf,0,hasRead);
            }
            raf.seek(0);
            raf.write(content.getBytes());
            //追加临时文件的内容
            while((hasRead = tmpIn.read(buf))>0){
                raf.write(buf,0,hasRead);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                deleteFile(childFiles[i].getAbsolutePath());
            }
            file.delete();
        }
    }

}
