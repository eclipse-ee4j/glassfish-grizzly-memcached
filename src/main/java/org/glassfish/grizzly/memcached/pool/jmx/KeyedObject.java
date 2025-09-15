/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

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
public class KeyedObject<K, V> extends JmxObject {

    private static final CompositeObjectStat INVALID_STAT = new CompositeObjectStat(-1, -1, -1, -1);

    private final BaseObjectPool<K, V> pool;
    private final K key;

    public KeyedObject(final BaseObjectPool<K, V> pool, final K key) {
        this.pool = pool;
        this.key = key;
    }

    @Override
    public String getJmxName() {
        final BaseObjectPool.QueuePool<K, V> queuePool = pool.getPool(key);
        if (queuePool == null) {
            return "(invalid)" + key;
        }
        return queuePool.getName();
    }

    @Override
    protected void onRegister(GrizzlyJmxManager mom, GmbalMBean bean) {
    }

    @Override
    protected void onDeregister(GrizzlyJmxManager mom) {
    }

    @ManagedAttribute(id = "keyed-object-type")
    @Description("The Java type of the keyed object implementation being used.")
    public String getKeyedObjectType() {
        final BaseObjectPool.QueuePool<K, V> queuePool = pool.getPool(key);
        if (queuePool == null) {
            return "(invalid)" + BaseObjectPool.QueuePool.class.getName();
        }
        return queuePool.getClass().getName();
    }

    @ManagedAttribute(id = "object-stat")
    @Description("The stat of objects in this pool.")
    public CompositeObjectStat getObjectStat() {
        final BaseObjectPool.QueuePool<K, V> queuePool = pool.getPool(key);
        if (queuePool == null) {
            return INVALID_STAT;
        }
        return new CompositeObjectStat(queuePool.getPoolSize(), queuePool.getPeakCount(), queuePool.getActiveCount(),
                                       queuePool.getIdleCount());
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
