package com.swin.worker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Distributor implements Worker {

    private String dbName = "distributor.db";
    private String dbDirectory;

    private Map<String, String> userMap;
    private Map<String, Integer> queueSequence;
    private Map<String, String> queueSum;

    public Distributor(String dbDirectory) {
        this.dbDirectory = dbDirectory;
        this.userMap = new ConcurrentHashMap<>();
        this.queueSequence = new ConcurrentHashMap<>();
        this.queueSum = new ConcurrentHashMap<>();
    }

    public void subscription(String user, String queueName) {
        if (userMap.containsKey(user)) {

        }
    }

    public void subscription(String user, String queueName, Integer sequence) {
        if (userMap.containsKey(user)) {

        }
    }


    @Override
    public void run() {

    }

    class SubscriptionTree {
        private String queueName;
        private Integer queueSequence;
        private Integer keySequence;

        private SubscriptionTree(String queueName) {
            this.queueName = queueName;
            this.queueSequence = 0;
            this.keySequence = 0;
        }

        private void queueAdd() {
            this.queueSequence++;
        }

        private void keyAdd() {
            this.keySequence++;
        }

        private Integer getQueueSequence() {
            return this.queueSequence;
        }

        private Integer getKeySequence() {
            return this.keySequence;
        }

    }
}
