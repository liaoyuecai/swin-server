package com.swin.db;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LiaoYuecai on 2018/1/24.
 */
public class DBQueueWorker implements Runnable {

    private static final int MAX_KEYS = 1000 * 10000;

    private String dbFileName;

    private String dbDirectory;

    private Set<String> real;//format : "treeName.key"
    private Set<String> old;
    private Set<String> useless;

    private long interval;

    private Map<String, Map<Integer, Object>> queueMap;
    private Map<String, Sequence> queueSeq;
    private Map<String, Subscription> subscriptionMap;

    public DBQueueWorker(String dbDirectory, long interval) {
        this.dbDirectory = dbDirectory;
        this.interval = interval;
        this.real = new HashSet<>();
        this.old = new HashSet<>();
        this.useless = new HashSet<>();
        this.queueMap = new ConcurrentHashMap<>();
        this.queueSeq = new ConcurrentHashMap<>();
        this.subscriptionMap = new ConcurrentHashMap<>();
    }

    public String getQueueNameAndSeq(String treeName) throws Exception {
        Sequence seq = queueSeq.get(treeName);
        if (seq != null) {
            return seq.queueName + "_" + seq.queueSeq + "." + seq.keySeq;
        } else {
            throw new Exception();
        }
    }

    public void putData(String queueName, Integer sequence, Object data) {
        Sequence seq = queueSeq.get(queueName);
        if (seq != null) {
            if (seq.keySeq == MAX_KEYS) {
                seq.queueSeq++;
                seq.keySeq = 0;
            } else {
                seq.keySeq++;
            }
        } else {
            seq = new Sequence(queueName, 0, 0);
            queueSeq.put(queueName, seq);
        }
        String treeName = seq.queueName + "_" + seq.queueSeq;
        Map<Integer, Object> tree = queueMap.get(treeName);
        if (tree == null) {
            tree = new ConcurrentHashMap();
            queueMap.put(treeName, tree);
        }
        tree.put(seq.keySeq, data);
        real.add(treeName + "." + seq.keySeq);
    }

    public Object getData(String treeName, Integer sequence) {
        return queueMap.get(treeName).get(sequence);
    }

    private void removeData(String treeName, Integer sequence) {
        queueMap.get(treeName).remove(sequence);
    }

    public void subscription(String consumer, String treeName) {
        String seq = "";//getQueueNameAndSeq
        String[] strings = seq.split("\\.");
        subscriptionMap.put(consumer + "_" + treeName, new Subscription(consumer, strings[0], Integer.parseInt(strings[1])));
    }

    public void cancelSub(String consumer, String treeName) {
        subscriptionMap.remove(consumer + "_" + treeName);
    }

    @Override
    public void run() {
        while (true) {
            if (!useless.isEmpty()) {
                String[] strings;
                for (String str : useless) {
                    strings = str.split("\\.");
                    this.removeData(strings[0], Integer.parseInt(strings[1]));
                }
                old.remove(useless);
                useless.clear();
            }
            useless.addAll(old);
            real.remove(old);
            old.addAll(real);
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Sequence {
        private String queueName;
        private int queueSeq;
        private int keySeq;

        public Sequence(String queueName, int queueSeq, int keySeq) {
            this.queueName = queueName;
            this.queueSeq = queueSeq;
            this.keySeq = keySeq;
        }
    }

    class Subscription {
        private String consumer;
        private String treeName;
        private Integer seq;

        public Subscription(String consumer, String treeName, Integer seq) {
            this.consumer = consumer;
            this.treeName = treeName;
            this.seq = seq;
        }
    }
}
