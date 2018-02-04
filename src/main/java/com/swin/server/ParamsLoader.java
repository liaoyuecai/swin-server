package com.swin.server;

import com.swin.exception.ServerStartException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ParamsLoader {

    private static String dbDirectory;
    private static String backupsDirectory;
    private static Integer port;

    public static void loader() throws Exception {
        String filePath = System.getProperty("user.dir") + "/server.properties";
        File file = new File(filePath);
        if (!file.exists()) {
            throw new ServerStartException("Server start fail, missing configuration file 'server.properties'");
        }
        InputStream in = new FileInputStream(file);
        Properties prop = new Properties();
        prop.load(in);

        dbDirectory = System.getProperty("user.dir") + prop.getProperty("dbDirectory");
        if (dbDirectory == null) {
            if (!file.exists()) {
                throw new ServerStartException("Server start fail, missing configuration 'dbDirectory'");
            }
        }
        backupsDirectory = System.getProperty("user.dir") + prop.getProperty("backupsDirectory");
        if (backupsDirectory == null) {
            if (!file.exists()) {
                throw new ServerStartException("Server start fail, missing configuration 'backupsDirectory'");
            }
        }
        port = Integer.valueOf(prop.getProperty("port"));
        if (port == null) {
            if (!file.exists()) {
                throw new ServerStartException("Server start fail, missing configuration 'port'");
            }
        }
    }

    public static String getDbDirectory() {
        return dbDirectory;
    }

    public static String getBackupsDirectory() {
        return backupsDirectory;
    }

    public static Integer getPort() {
        return port;
    }
}
