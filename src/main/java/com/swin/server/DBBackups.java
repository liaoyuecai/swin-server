//package com.swin.server;
//
//import com.swin.db.MapDBFactory;
//import com.swin.utils.FileUtils;
//import org.mapdb.DB;
//import org.mapdb.DBMaker;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.util.Map;
//
//public class DBBackups {
//    private static long interval = 60 * 1000 * 5;
//    private static final Logger logger = LoggerFactory.getLogger(DBBackups.class);
//    private static String mapDBPath;
//    private static String mapDBBackupsPath;
//
//    public static void init() {
//        mapDBPath = ParamsLoader.getDbDirectory() + "/mapDB.db";
//        mapDBBackupsPath = ParamsLoader.getBackupsDirectory() + "/mapDB.db";
//        ServerThreadPool.execute(new Thread() {
//            @Override
//            public void run() {
//                while (true) {
//                    backups(MapDBFactory.getDB(), mapDBPath, mapDBBackupsPath);
//                    try {
//                        Thread.sleep(interval);
//                    } catch (InterruptedException e) {
//                        logger.error("Sleep error", e);
//                    }
//                }
//            }
//        });
//    }
//
//    private static void backups(DB srcDb, String srcFilePath, String backups) {
//        try {
//            if (srcDb != null) {
//                File file = new File(backups);
//                if (!file.getParentFile().exists()) {
//                    file.getParentFile().mkdirs();
//                }
//                DB db = DBMaker
//                        .fileDB(file).closeOnJvmShutdown()
//                        .make();
//                Map map = null;
//                Map srcMap = null;
//                for (String treeName : srcDb.getAllNames()) {
//                    srcMap = srcDb.treeMap(treeName).open();
//                    if (db.exists(treeName)) {
//                        map = db.treeMap(treeName).open();
//                    } else {
//                        map = db.treeMap(treeName).create();
//                    }
//                    map.putAll(srcMap);
//                }
//                db.commit();
//                db.close();
//            } else {
//                logger.warn("DB been broken, you may have lost a few minutes of data, try to restore backups");
//                FileUtils.copyFile(backups, srcFilePath);
//                logger.info("Restore backups succeed");
//            }
//        } catch (Exception e) {
//            logger.error("DB backups error ", e);
//        }
//    }
//}
