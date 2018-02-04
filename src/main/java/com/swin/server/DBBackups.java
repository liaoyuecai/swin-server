package com.swin.server;

import com.swin.utils.FileUtils;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBBackups {
    private static long interval = 60 * 1000 * 5;
    private static final Logger logger = LoggerFactory.getLogger(DBBackups.class);
    private static String mapDBPath;
    private static String mapDBBackupsPath;

    public static void init() {
        mapDBPath = ParamsLoader.getDbDirectory() + "/mapDB.db";
        mapDBBackupsPath = ParamsLoader.getBackupsDirectory() + "/mapDB.db";
        ServerThreadPool.execute(new Thread() {
            @Override
            public void run() {
                while (true) {
                    backups(mapDBPath, mapDBBackupsPath);
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        logger.error("Sleep error", e);
                    }
                }
            }
        });
    }

    private static void backups(String src, String backups) {
        try {
            DBMaker
                    .fileDB(src).closeOnJvmShutdown()
                    .make();
            FileUtils.copyFile(src, backups);
        } catch (Exception e) {
            logger.error("DB backups error ", e);
        }
    }
}
