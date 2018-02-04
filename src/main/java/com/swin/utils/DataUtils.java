package com.swin.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataUtils {

    /**
     * Small to large array by value
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Keys to get the minimum by value
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V extends Comparable<? super V>> K getMinKeyByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        return list.get(0).getKey();
    }


    public static void main(String[] args) {
        Map<Integer, Integer> map = new ConcurrentHashMap<>();
        map.put(1,2);
        map.put(3,4);
        map.put(2,1);
        map.put(4,6);
        map.put(7,0);
        Map<Integer, Integer> sort = DataUtils.sortByValue(map);
        for (Integer i:sort.keySet()){
            System.out.println(i+":"+sort.get(i));
        }
        System.out.println(getMinKeyByValue(map));
    }
}
