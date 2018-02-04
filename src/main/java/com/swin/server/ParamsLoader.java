package com.swin.server;

import com.swin.exception.ServerStartException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ParamsLoader {

    private static String dbDirectory;
    private static Integer port;

    static {
        try {
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
            port = Integer.valueOf(prop.getProperty("port"));
            if (port == null) {
                if (!file.exists()) {
                    throw new ServerStartException("Server start fail, missing configuration 'port'");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDbDirectory() {
        return dbDirectory;
    }


    public static Integer getPort() {
        return port;
    }
}
