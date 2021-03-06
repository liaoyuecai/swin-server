package com.swin.server;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class MapDBFactory {
    final static Logger logger = LoggerFactory.getLogger(MapDBFactory.class);

    static DB db;
    static DB backups;

    static final long COMMIT_INTERVAL = 1 * 60 * 60 * 1000;

    static Map<String, BTreeMap<String, byte[]>> cacheTree;

    static {
        cacheTree = new ConcurrentHashMap<>();
        db = DBMaker
                .memoryDB().closeOnJvmShutdownWeakReference()
                .make();
    }


    static void init() throws Exception {
        File file = new File(ParamsLoader.getDbDirectory() + "/mapDB.db");
        if (file.exists()) {
            backups = DBMaker
                    .fileDB(file).closeOnJvmShutdownWeakReference()
                    .make();
            Map map = null;
            Map srcMap = null;
            for (String treeName : backups.getAllNames()) {
                srcMap = backups.treeMap(treeName).open();
                if (db.exists(treeName)) {
                    map = db.treeMap(treeName).open();
                } else {
                    map = db.treeMap(treeName).create();
                }
                map.putAll(srcMap);
            }
        } else {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            backups = DBMaker
                    .fileDB(file).closeOnJvmShutdownWeakReference()
                    .make();
        }
        BTreeMap<String, byte[]> treeMap = null;
        for (String treeName : db.getAllNames()) {
            treeMap = (BTreeMap<String, byte[]>) db.treeMap(treeName).open();
            cacheTree.put(treeName, treeMap);
        }
        ServerThreadPool.execute(new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        commit();
                        Thread.sleep(COMMIT_INTERVAL);
                    } catch (Exception e) {
                        logger.error("map db commit error", e);
                    }
                }
            }
        });
    }

    /**
     * Update or add data
     *
     * @param treeName
     * @param key
     * @param data
     */
    static void addOrUpdate(String treeName, String key, byte[] data) {
        if (cacheTree.containsKey(treeName)) {
            cacheTree.get(treeName).put(key, data);
        } else {
            BTreeMap<String, byte[]> treeMap = null;
            try {
                treeMap = (BTreeMap<String, byte[]>) db.treeMap(treeName).create();
            } catch (Exception e) {
                treeMap = (BTreeMap<String, byte[]>) db.treeMap(treeName).open();
            }
            treeMap.put(key, data);
            cacheTree.put(treeName, treeMap);
        }
        ClientManager.informTree(treeName, ClientManager.LISTEN_OPERATE.UPDATE, key, data);
    }

    /**
     * Remove a key
     *
     * @param treeName
     * @param mapKey
     */
    static void remove(String treeName, String mapKey) {
        if (cacheTree.containsKey(treeName) && cacheTree.get(treeName).containsKey(mapKey)) {
            ClientManager.informTree(treeName, ClientManager.LISTEN_OPERATE.DELETE, mapKey, cacheTree.get(treeName).get(mapKey));
        }
        cacheTree.get(treeName).remove(mapKey);
    }

    static byte[] getDataByTreeAndKey(String treeName, String mapKey) {
        if (cacheTree.containsKey(treeName)) {
            return cacheTree.get(treeName).get(mapKey);
        } else {
            return null;
        }
    }


    static void commit() throws Exception {
        Map map = null;
        Map srcMap = null;
        for (String tree : cacheTree.keySet()) {
            srcMap = cacheTree.get(tree);
            if (backups.exists(tree)) {
                map = backups.treeMap(tree).open();
            } else {
                map = backups.treeMap(tree).create();
            }
            map.putAll(srcMap);
        }
        backups.commit();
    }

}
