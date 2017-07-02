package com.example.sunshanxun.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by SunZJ on 2016/10/31.
 */

public class Fileutil {
    /**
     * 如果文件不存在，就创建文件
     *
     * @param filePath 文件路径
     * @return
     */
    public static String createIfNotExist(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return filePath;
    }

    /**
     * 向文件中写入数据
     *
     * @param filePath 目标文件全路径
     * @param data     要写入的数据
     * @return true表示写入成功  false表示写入失败
     */
    public static boolean writeBytes(String filePath, byte[] data) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(data);
            fos.close();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * 从文件中读取数据
     *
     * @param filePath
     * @return
     */
    public static byte[] readBytes(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        try {
            FileInputStream fis = new FileInputStream(filePath);
            int len = fis.available();
            byte[] buffer = new byte[len];
            fis.read(buffer);
            fis.close();
            return buffer;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;

    }

    /**
     * 向文件中写入字符串String类型的内容
     *
     * @param filePath 文件路径
     * @param content  文件内容
     * @param charset  写入时候所使用的字符集
     */
    public static void writeString(String filePath, String content, String charset) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        try {
            byte[] data = content.getBytes();
            writeBytes(filePath, data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * 追加文件：使用FileWriter
     *
     * @param filePath
     * @param content
     */
    public static void writeStringAppend(String filePath, String content) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(filePath, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件中读取数据，返回类型是字符串String类型
     *
     * @param filePath 文件路径
     * @param charset  读取文件时使用的字符集，如utf-8、GBK等
     * @return
     */
    public static String readString(String filePath, String charset) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        byte[] data = readBytes(filePath);
        String ret = null;

        try {
            ret = new String(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ret;
    }
}
