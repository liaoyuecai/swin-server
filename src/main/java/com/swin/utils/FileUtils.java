package com.swin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    public static void copyFile(String fromFile, String toFile) throws IOException {
        FileInputStream ins = new FileInputStream(fromFile);
        File file = createFile(toFile);
        FileOutputStream out = new FileOutputStream(file);
        byte[] b = new byte[1024];
        int n = 0;
        while ((n = ins.read(b)) != -1) {
            out.write(b, 0, n);
        }
        ins.close();
        out.close();
    }

    public static File createFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        return file;
    }

    public static void saveFile(String filePath, byte[] data) throws IOException {
        File file = createFile(filePath);
        FileOutputStream out = new FileOutputStream(file);
        out.write(data);
        out.close();
    }
}
