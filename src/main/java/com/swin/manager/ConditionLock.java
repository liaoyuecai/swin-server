package com.swin.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by LiaoYuecai on 2018/1/26.
 */
public class ConditionLock {
    private static ConditionLock conditionLock = new ConditionLock();
    private Lock lock;
    private Map<String, Task> conditionMap;

    private ConditionLock() {
        lock = new ReentrantLock();
        conditionMap = new ConcurrentHashMap<>();
    }

    public static ConditionLock getInstance() {
        return conditionLock;
    }

    public Object await(String key,long timeout) {
        try {
            Condition con = lock.newCondition();
            this.lock.lock();
            Task task = new Task(con);
            conditionMap.put(key, task);
            boolean flag = con.await(timeout, TimeUnit.MILLISECONDS);
            if (!flag){
                throw new Exception();
            }
            return task.result;
        } catch (Exception e) {
            return null;
        } finally {
            conditionMap.remove(key);
            this.lock.unlock();
        }
    }

    public void release(String key, Object result) {
        try {
            Condition con = conditionMap.get(key).con;
            conditionMap.get(key).result = result;
            this.lock.lock();
            con.signal();
        } catch (Exception e) {

        } finally {
            this.lock.unlock();
        }
    }

    class Task {
        private Condition con;
        private Object result;

        public Task(Condition con) {
            this.con = con;
        }
    }

}
