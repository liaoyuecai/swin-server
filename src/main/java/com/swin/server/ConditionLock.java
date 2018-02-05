package com.swin.server;

import com.swin.exception.ConditionTaskException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by LiaoYuecai on 2018/1/26.
 */
class ConditionLock {
    private static Lock lock;
    private static Map<String, Task> conditionMap;
    private static Map<String, AsynchronousData> sysMap;

    static {
        lock = new ReentrantLock();
        conditionMap = new ConcurrentHashMap<>();
        sysMap = new ConcurrentHashMap<>();
    }


    static Object await(String key, long timeout) throws Exception {
        if (sysMap.containsKey(key)) {
            AsynchronousData data = sysMap.get(key);
            sysMap.remove(key);
            return data.result;
        }
        try {
            if (conditionMap.containsKey(key)) {
                throw new ConditionTaskException("This task is exist");
            }
            Condition con = lock.newCondition();
            lock.lock();
            Task task = new Task(con);
            conditionMap.put(key, task);
            boolean flag = con.await(timeout, TimeUnit.MILLISECONDS);
            if (!flag) {
                throw new ConditionTaskException("Time out");
            }
            return task.result;
        } catch (Exception e) {
            throw e;
        } finally {
            conditionMap.remove(key);
            lock.unlock();
        }
    }

    static void release(String key, Object result) {
        if (conditionMap.containsKey(key)) {
            try {
                Condition con = conditionMap.get(key).con;
                conditionMap.get(key).result = result;
                lock.lock();
                con.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        } else {
            sysMap.put(key, new AsynchronousData(result, System.currentTimeMillis()));
        }
    }

    static class Task {
        private Condition con;
        private Object result;

        public Task(Condition con) {
            this.con = con;
        }
    }

    static class AsynchronousData {
        private Object result;
        private long time;

        public AsynchronousData(Object result, long time) {
            this.result = result;
            this.time = time;
        }
    }
}
