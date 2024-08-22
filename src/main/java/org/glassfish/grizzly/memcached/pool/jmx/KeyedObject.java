package org.glassfish.grizzly.memcached.pool.jmx;

import org.glassfish.gmbal.Description;
import org.glassfish.gmbal.GmbalMBean;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;
import org.glassfish.gmbal.ManagedObject;
import org.glassfish.grizzly.jmxbase.GrizzlyJmxManager;
import org.glassfish.grizzly.memcached.pool.BaseObjectPool;
import org.glassfish.grizzly.monitoring.jmx.JmxObject;

/**
 * JMX managed object for object pool's keyed object implementations.
 */
@ManagedObject
@Description("Keyed object managed in the basic object pool(generally related to network connection)")
public class KeyedObject extends JmxObject {

    private final BaseObjectPool.QueuePool pool;

    public KeyedObject(final BaseObjectPool.QueuePool pool) {
        this.pool = pool;
    }

    @Override
    public String getJmxName() {
        return pool.getName();
    }

    @Override
    protected void onRegister(GrizzlyJmxManager mom, GmbalMBean bean) {
    }

    @Override
    protected void onDeregister(GrizzlyJmxManager mom) {
    }

    /**
     * Returns the Java type of the managed object pool
     *
     * @return the Java type of the managed object pool.
     */
    @ManagedAttribute(id = "keyed-object-type")
    @Description("The Java type of the keyed object implementation being used.")
    public String getKeyedObjectType() {
        return pool.getClass().getName();
    }

    @ManagedAttribute(id = "object-stat")
    @Description("The stat of objects in this pool.")
    public CompositeObjectStat getObjectStat() {
        return new CompositeObjectStat(pool.getPoolSize(), pool.getPeakCount(), pool.getActiveCount(),
                                       pool.getIdleCount());
    }

    @ManagedData(name="Object Stat")
    private static class CompositeObjectStat {
        @ManagedAttribute(id = "objects")
        @Description("The total number of objects currently idle and active in this pool or a negative value if unsupported.")
        private final int objectSize;

        @ManagedAttribute(id = "peak")
        @Description("The peak number of objects or a negative value if unsupported.")
        private final int peakCount;

        @ManagedAttribute(id = "active")
        @Description("The number of objects currently borrowed in this pool or a negative value if unsupported.")
        private final int activeCount;

        @ManagedAttribute(id = "idle")
        @Description("The number of objects currently idle in this pool or a negative value if unsupported.")
        private final int idleCount;

        private CompositeObjectStat(int objectSize, int peakCount, int activeCount, int idleCount) {
            this.objectSize = objectSize;
            this.peakCount = peakCount;
            this.activeCount = activeCount;
            this.idleCount = idleCount;
        }
    }
}
