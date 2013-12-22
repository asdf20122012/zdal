package com.alipay.zdal.common.util;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

/**
 * @author dogun
 *
 */
public final class ZdalMBeanServer {
    private static final Logger                                                     log       = Logger
                                                                                                  .getLogger(ZdalMBeanServer.class);
    private static final String                                                     LogPrefix = "[TDDLMBeanServer]";

    private static MBeanServer                                                      mbs       = null;

    private static ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicLong>> idMap     = new ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicLong>>();
    private static ReentrantLock                                                    lock      = new ReentrantLock();

    //private static MyMBeanServer me = new MyMBeanServer();

    private ZdalMBeanServer() {
        //´´½¨MBServer
        String hostName = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();

            hostName = addr.getHostName();
        } catch (IOException e) {
            log.error(LogPrefix + "Get HostName Error", e);
            hostName = "localhost";
        }
        String host = System.getProperty("hostName", hostName);
        try {
            boolean useJmx = Boolean.parseBoolean(System.getProperty("tddl.useJMX", "true"));
            if (useJmx) {
                mbs = ManagementFactory.getPlatformMBeanServer();
                int port = Integer.parseInt(System.getProperty("tddl.rmi.port", "6679"));
                String rmiName = System.getProperty("tddl.rmi.name", "tddlJmxServer");
                Registry reg = null;
                try {
                    reg = LocateRegistry.getRegistry(port);
                    reg.list();
                } catch (Exception e) {
                    reg = null;
                }
                if (null == reg) {
                    reg = LocateRegistry.createRegistry(port);
                }
                reg.list();
                String serverURL = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/"
                                   + rmiName;
                JMXServiceURL url = new JMXServiceURL(serverURL);
                final JMXConnectorServer connectorServer = JMXConnectorServerFactory
                    .newJMXConnectorServer(url, null, mbs);
                connectorServer.start();
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        try {

                            log.error("JMXConnector stop");
                            connectorServer.stop();
                        } catch (IOException e) {
                            log.error(LogPrefix, e);
                        }
                    }
                });
                log.warn(LogPrefix + "jmx url: " + serverURL);
            }
        } catch (Exception e) {
            log.error(LogPrefix + "create MBServer error", e);
        }
    }

    /*public static TDDLMBeanServer getInstance() {
    	return Holder.instance;
    }*/
    public static void registerMBean(Object o, String name) {
        registerMBean0(o, name);
    }

    public static void registerMBeanWithId(Object o, String id) {
        registerMBeanWithId0(o, id);
    }

    public static void registerMBeanWithIdPrefix(Object o, String idPrefix) {
        registerMBeanWithIdPrefix0(o, idPrefix);
    }

    private static void registerMBean0(Object o, String name) {
        //×¢²áMBean
        if (null != mbs) {
            try {
                mbs.registerMBean(o, new ObjectName(o.getClass().getPackage().getName()
                                                    + ":type="
                                                    + o.getClass().getSimpleName()
                                                    + (null == name ? (",id=" + o.hashCode())
                                                        : (",name=" + name + "-" + o.hashCode()))));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void registerMBeanWithId0(Object o, String id) {
        //×¢²áMBean
        if (null == id || id.length() == 0) {
            throw new IllegalArgumentException("must set id");
        }
        if (null != mbs) {
            try {
                mbs.registerMBean(o, new ObjectName(o.getClass().getPackage().getName() + ":type="
                                                    + o.getClass().getSimpleName() + ",id=" + id));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String getId(String name, String idPrefix) {
        ConcurrentHashMap<String, AtomicLong> subMap = idMap.get(name);
        if (null == subMap) {
            lock.lock();
            try {
                subMap = idMap.get(name);
                if (null == subMap) {
                    subMap = new ConcurrentHashMap<String, AtomicLong>();
                    idMap.put(name, subMap);
                }
            } finally {
                lock.unlock();
            }
        }

        AtomicLong indexValue = subMap.get(idPrefix);
        if (null == indexValue) {
            lock.lock();
            try {
                indexValue = subMap.get(idPrefix);
                if (null == indexValue) {
                    indexValue = new AtomicLong(0);
                    subMap.put(idPrefix, indexValue);
                }
            } finally {
                lock.unlock();
            }
        }
        long value = indexValue.incrementAndGet();
        String result = idPrefix + "-" + value;
        return result;
    }

    private static void registerMBeanWithIdPrefix0(Object o, String idPrefix) {
        //×¢²áMBean
        if (null != mbs) {
            if (null == idPrefix || idPrefix.length() == 0) {
                idPrefix = "default";
            }
            idPrefix = idPrefix.replace(":", "-");

            try {
                String id = getId(o.getClass().getName(), idPrefix);

                mbs.registerMBean(o, new ObjectName(o.getClass().getPackage().getName() + ":type="
                                                    + o.getClass().getSimpleName() + ",id=" + id));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
