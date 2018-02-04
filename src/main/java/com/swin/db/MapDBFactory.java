package com.swin.db;

import com.swin.server.ParamsLoader;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapDBFactory {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static DB db;

    private static Map<String, BTreeMap<String, byte[]>> cacheTree;

    public synchronized static void init() {
        if (db == null) {
            cacheTree = new ConcurrentHashMap<>();
            File file = new File(ParamsLoader.getDbDirectory() + "/mapDB.db");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            db = DBMaker
                    .fileDB(file).closeOnJvmShutdown()
                    .make();
            BTreeMap<String, byte[]> treeMap = null;
            for (String treeName : db.getAllNames()) {
                treeMap = (BTreeMap<String, byte[]>) db.treeMap(treeName).open();
                cacheTree.put(treeName, treeMap);
            }
        }
    }

    /**
     * Update or add data
     *
     * @param treeName
     * @param key
     * @param data
     */
    public static void addOrUpdate(String treeName, String key, byte[] data) {
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
    }

    /**
     * Remove a key
     *
     * @param treeName
     * @param mapKey
     */
    public static void remove(String treeName, String mapKey) {
        cacheTree.get(treeName).remove(mapKey);
    }

    public static byte[] getDataByTreeAndKey(String treeName, String mapKey) {
        if (cacheTree.containsKey(treeName)) {
            return cacheTree.get(treeName).get(mapKey);
        } else {
            return null;
        }
    }


    public static void commit() {
        for (String tree : cacheTree.keySet()) {
            cacheTree.get(tree).close();
        }
        db.commit();
        db.close();
        init();
    }
}
