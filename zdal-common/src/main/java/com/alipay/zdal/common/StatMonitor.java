package com.alipay.zdal.common;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.alipay.zdal.common.util.NagiosUtils;

/*
 * @author guangxia
 * @since 1.0, 2010-2-8 下午04:18:39
 */
public class StatMonitor implements StatMonitorMBean {

    private static final Logger      logger       = Logger.getLogger(StatMonitor.class);

    //单位是毫秒
    private volatile long            statInterval = 5 * 60 * 1000;
    private Set<String>              blackList    = new HashSet<String>(0);
    private int                      limit        = 1000;

    private static final StatMonitor instance     = new StatMonitor();

    private StatMonitor() {
    }

    public static StatMonitor getInstance() {
        return instance;
    }

    static class Value2 {
        final StatCounter                            statCounter = new StatCounter();
        final ConcurrentHashMap<String, StatCounter> map2        = new ConcurrentHashMap<String, StatCounter>();

        public String toString() {
            return map2.toString();
        }
    }

    static class Value1 {
        final StatCounter                       statCounter = new StatCounter();
        final ConcurrentHashMap<String, Value2> map1        = new ConcurrentHashMap<String, Value2>();

        public String toString() {
            return map1.toString();
        }
    }

    static class Value0 {
        final StatCounter                       statCounter = new StatCounter();
        final ConcurrentHashMap<String, Value1> map0        = new ConcurrentHashMap<String, Value1>();

        public String toString() {
            return map0.toString();
        }
    }

    static class State {
        final Value0        currentStatMap = new Value0();
        final Value0        lastStatMap;
        final AtomicInteger size           = new AtomicInteger(0);
        final long          lastResetTime  = System.currentTimeMillis();

        State() {
            lastStatMap = currentStatMap;
        }

        State(final State lastState) {
            lastStatMap = lastState.currentStatMap;
        }

        public String toString() {
            return lastStatMap.toString();
        }
    }

    private volatile State state = new State();

    public String toString() {
        return state.toString();
    }

    static class StatCounter {
        private final AtomicLong count = new AtomicLong(0L);
        private final AtomicLong value = new AtomicLong(0L);

        public void incrementCount() {
            this.count.incrementAndGet();
        }

        public void addValue(long value) {
            this.value.addAndGet(value);
        }

        public synchronized void reset() {
            this.count.set(0L);
            this.value.set(0L);
        }

        public String toString() {
            return "{StatCounter, " + count.get() + ", " + value.get() + "}";
        }
    }

    private Thread restTask = new Thread() {
                                @Override
                                public void run() {
                                    while (!Thread.currentThread().isInterrupted()) {
                                        try {
                                            Thread.sleep(statInterval);
                                        } catch (InterruptedException e) {
                                            Thread.currentThread().interrupt();
                                        }

                                        StatMonitor.State old = resetState();
                                        writeMonitor(old);
                                    }
                                }
                            };

    private void writeMonitor(State old) {
        try {
            for (Map.Entry<String, Value1> e0 : old.lastStatMap.map0.entrySet()) {
                String key1 = e0.getKey();
                for (Map.Entry<String, Value2> e1 : e0.getValue().map1.entrySet()) {
                    String key2 = e1.getKey();
                    three: for (Map.Entry<String, StatCounter> e2 : e1.getValue().map2.entrySet()) {
                        String key3 = e2.getKey();

                        StatCounter counter = e2.getValue();
                        if (counter == null) {
                            continue three;
                        }
                        long count = counter.count.get();
                        long values = counter.value.get();
                        String averageValueStr = "invalid";
                        if (count != 0) {
                            double averageValue = (double) values / count;
                            averageValueStr = String.valueOf(averageValue);
                        }
                        NagiosUtils.addNagiosLog(key1 + "|" + key2 + "|" + key3, averageValueStr);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("WARN ## ", e);
        }
    }

    private boolean started = false;

    public synchronized void start() {
        if (started) {
            return;
        }
        restTask.start();
        started = true;
        if (logger.isDebugEnabled()) {
            logger.debug("INFO ## StatMonitor start...");
        }
    }

    public void stop() {
        restTask.interrupt();
        while (restTask.isAlive()) {
            try {
                restTask.join();
            } catch (InterruptedException e) {

            }
        }
    }

    private synchronized final State resetState() {
        State old = state;
        state = new State(state);
        return old;
    }

    private final String getLastStatResult(String key1, String key2, String key3) {
        State local_state = state;
        Value1 value1 = local_state.lastStatMap.map0.get(key1);
        if (value1 == null) {
            logger.warn("WARN ## getLastStatResult(" + key1 + ", " + key2 + ", " + key3
                        + ") Invalid key1: " + key1);
            return null;
        }
        Value2 value2 = value1.map1.get(key2);
        if (value2 == null) {
            logger.warn("WARN ## getLastStatResult(" + key1 + ", " + key2 + ", " + key3
                        + ") Invalid key2: " + key2);
            return null;
        }
        StatCounter counter = value2.map2.get(key3);
        if (counter == null) {
            logger.warn("WARN ## getLastStatResult(" + key1 + ", " + key2 + ", " + key3
                        + ") Invalid key3: " + key3);
            return null;
        }
        long count = counter.count.get();
        long values = counter.value.get();
        String averageValueStr = "invalid";
        String averageCountStr = "invalid";
        if (count != 0) {
            double averageValue = (double) values / count;
            averageValueStr = String.valueOf(averageValue);
        }
        long duration;
        if (local_state.lastStatMap != local_state.currentStatMap) {
            duration = statInterval;
        } else {
            duration = System.currentTimeMillis() - local_state.lastResetTime;
        }
        if (duration != 0) {
            double averageCount = (double) (count * 1000) / duration;
            averageCountStr = String.valueOf(averageCount);
        }
        return "count: " + count + ", value: " + values + ", average: " + averageValueStr
               + ", Count/Duration: " + averageCountStr;
    }

    static class Item {
        final String key1;
        final String key2;
        final String key3;
        final long   count;
        final long   value;

        public Item(String key1, String key2, String key3, long count, long value) {
            this.key1 = key1;
            this.key2 = key2;
            this.key3 = key3;
            this.count = count;
            this.value = value;
        }

        public String toString() {
            return "{(" + key1 + ", " + key2 + ", " + key3 + "), count: " + count + ", value: "
                   + value + "}";
        }
    }

    static class CountComparator implements Comparator<Item> {
        public int compare(Item o1, Item o2) {
            int ret = -((Long) o1.count).compareTo(o2.count);
            if (ret != 0) {
                return ret;
            }
            if (o1 == o2) {
                return ret;
            }
            return ((Integer) System.identityHashCode(o1)).compareTo(System.identityHashCode(o2));
        }
    }

    static class ValueComparator implements Comparator<Item> {
        public int compare(Item o1, Item o2) {
            int ret = -((Long) o1.value).compareTo(o2.value);
            if (ret != 0) {
                return ret;
            }
            if (o1 == o2) {
                return ret;
            }
            return ((Integer) System.identityHashCode(o1)).compareTo(System.identityHashCode(o2));
        }
    }

    final CountComparator countComparator = new CountComparator();
    final ValueComparator valueComparator = new ValueComparator();

    public SortedSet<Item> getSortedSetByCount(String key1, String key2, String key3) {
        State local_state = state;
        SortedSet<Item> sortedSet = new TreeSet<Item>(countComparator);
        visitMap0(sortedSet, local_state.lastStatMap.map0, key1, key2, key3);
        return sortedSet;
    }

    public SortedSet<Item> getSortedSetByValue(String key1, String key2, String key3) {
        State local_state = state;
        SortedSet<Item> sortedSet = new TreeSet<Item>(valueComparator);
        visitMap0(sortedSet, local_state.lastStatMap.map0, key1, key2, key3);
        return sortedSet;
    }

    void visitMap0(SortedSet<Item> sortedSet, ConcurrentHashMap<String, Value1> map0, String key1,
                   String key2, String key3) {
        if ("*".equals(key1)) {
            for (Map.Entry<String, Value1> entry : map0.entrySet()) {
                visitMap1(sortedSet, entry.getValue().map1, entry.getKey(), key2, key3);
            }
        } else {
            Value1 value1 = map0.get(key1);
            if (value1 != null) {
                visitMap1(sortedSet, value1.map1, key1, key2, key3);
            }
        }
    }

    void visitMap1(SortedSet<Item> sortedSet, ConcurrentHashMap<String, Value2> map1, String key1,
                   String key2, String key3) {
        if ("*".equals(key2)) {
            for (Map.Entry<String, Value2> entry : map1.entrySet()) {
                visitMap2(sortedSet, entry.getValue().map2, key1, entry.getKey(), key3);
            }
        } else {
            Value2 value2 = map1.get(key2);
            if (value2 != null) {
                visitMap2(sortedSet, value2.map2, key1, key2, key3);
            }
        }
    }

    void visitMap2(SortedSet<Item> sortedSet, ConcurrentHashMap<String, StatCounter> map2,
                   String key1, String key2, String key3) {
        if ("*".equals(key3)) {
            for (Map.Entry<String, StatCounter> entry : map2.entrySet()) {
                sortedSet.add(new Item(key1, key2, entry.getKey(), entry.getValue().count.get(),
                    entry.getValue().value.get()));
            }
        } else {
            StatCounter statCounter = map2.get(key3);
            if (statCounter != null) {
                sortedSet.add(new Item(key1, key2, key3, statCounter.count.get(), statCounter.value
                    .get()));
            }
        }
    }

    public long getDuration() {
        State local_state = state;
        return (System.currentTimeMillis() - local_state.lastResetTime) / 1000;
    }

    public final boolean addStat(String keyOne, String keyTwo, String keyThree) {
        return realTimeStat(keyOne, keyTwo, keyThree, 0);
    }

    private final boolean realTimeStat(String key1, String key2, String key3, long value) {
        if (blackList.contains(key1)) {
            return false;
        }
        return processMap2(key1, key2, key3, value);
    }

    private boolean processMap2(String key1, String key2, String key3, long value) {
        State local_state = state;
        Value1 value1 = local_state.currentStatMap.map0.get(key1);
        if (value1 == null) {
            value1 = new Value1();
            Value1 oldValue1 = local_state.currentStatMap.map0.putIfAbsent(key1, value1);
            if (oldValue1 != null) {
                value1 = oldValue1;
            }
        }
        Value2 value2 = value1.map1.get(key2);
        if (value2 == null) {
            value2 = new Value2();
            Value2 oldValue2 = value1.map1.putIfAbsent(key2, value2);
            if (oldValue2 != null) {
                value2 = oldValue2;
            }
        }

        StatCounter statCounter = value2.map2.get(key3);
        if (statCounter == null) {
            if (local_state.size.get() >= limit) {
                return false;
            }
            statCounter = new StatCounter();
            StatCounter oldCounter = value2.map2.putIfAbsent(key3, statCounter);
            if (oldCounter != null) {
                statCounter = oldCounter;
            } else {
                local_state.size.incrementAndGet();
            }
        }
        //暂时不对value2和value1的statCounter进行操作, 因为没有这个需求
        statCounter.incrementCount();
        statCounter.addValue(value);
        return true;
    }

    public final boolean addStat(String keyOne, String keyTwo, String keyThree, long value) {
        return realTimeStat(keyOne, keyTwo, keyThree, value);
    }

    public void setStatInterval(long statInterval) {
        this.statInterval = statInterval;
    }

    public long getStatInterval() {
        return statInterval;
    }

    public long getStatDuration() {
        State local_state = state;
        return local_state.lastResetTime;
    }

    public String getStatResult(String key1, String key2, String key3) {
        return getLastStatResult(key1, key2, key3);
    }

    public void resetStat() {
        resetState();
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    @SuppressWarnings("unchecked")
    public void setBlackList(Set<String> blackList) {
        if (!(blackList instanceof HashSet)) {
            blackList = new HashSet<String>(blackList);
        }
        this.blackList = blackList;
    }

    public Set<String> getBlackList() {
        return blackList;
    }

}
