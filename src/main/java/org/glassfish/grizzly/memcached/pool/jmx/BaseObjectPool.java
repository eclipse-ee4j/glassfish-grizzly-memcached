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

import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;
import org.glassfish.grizzly.jmxbase.GrizzlyJmxManager;
import org.glassfish.grizzly.monitoring.jmx.JmxObject;

import org.glassfish.gmbal.Description;
import org.glassfish.gmbal.GmbalMBean;
import org.glassfish.gmbal.ManagedObject;

import java.util.Collection;
import java.util.HashSet;

/**
 * JMX managed object for object pool implementations.
 */
@ManagedObject
@Description("Basic object pool with a pool of objects (generally related to network connections) for each key.")
public class BaseObjectPool<K, V> extends JmxObject {

    private final org.glassfish.grizzly.memcached.pool.BaseObjectPool<K, V> pool;

    private GrizzlyJmxManager mom;
    private final Collection<Object> keyedObjectJmx = new HashSet<>();

    public BaseObjectPool(final org.glassfish.grizzly.memcached.pool.BaseObjectPool<K, V> pool) {
        this.pool = pool;
    }

    @Override
    public String getJmxName() {
        return pool.getName();
    }

    @Override
    protected void onRegister(GrizzlyJmxManager mom, GmbalMBean bean) {
        this.mom = mom;
        rebuildSubTree();
    }

    @Override
    protected void onDeregister(GrizzlyJmxManager mom) {
        this.mom = null;
    }

    @ManagedAttribute(id = "object-pool-type")
    @Description("The Java type of the object pool implementation being used.")
    public String getPoolType() {
        return pool.getClass().getName();
    }

    @ManagedAttribute(id = "object-pool-stat")
    public CompositeObjectPoolStat getPoolStat() {
        return new CompositeObjectPoolStat(pool.getTotalPoolSize(), pool.getHighestPeakCount(),
                                           pool.getTotalActiveCount(), pool.getTotalIdleCount());
    }

    @ManagedAttribute(id = "object-pool-min-pool-size-per-key")
    @Description("The initial/minimum number of objects per key managed by this object pool.")
    public int getMinPerKey() {
        return pool.getMin();
    }

    @ManagedAttribute(id = "object-pool-max-pool-size-per-key")
    @Description("The maximum number of objects per key allowed by this object pool.")
    public int getMaxPerKey() {
        return pool.getMax();
    }

    @ManagedAttribute(id = "object-pool-borrow-validation")
    public boolean isBorrowValidation() {
        return pool.isBorrowValidation();
    }

    @ManagedAttribute(id = "object-pool-return-validation")
    public boolean isReturnValidation() {
        return pool.isReturnValidation();
    }

    @ManagedAttribute(id = "object-pool-disposable")
    public boolean isDisposable() {
        return pool.isDisposable();
    }

    @ManagedAttribute(id = "object-pool-keep-alive-timeout-seconds")
    public long getKeepAliveTimeoutInSecs() {
        return pool.getKeepAliveTimeoutInSecs();
    }

    @ManagedAttribute(id = "object-pool-destroyed")
    public boolean isDestroyed() {
        return pool.isDestroyed();
    }

    @ManagedAttribute(id = "object-pool-keys")
    public String getKeys() {
        return pool.getKeys();
    }

    private void rebuildSubTree() {
        keyedObjectJmx.forEach(jmxObj -> mom.deregister(jmxObj));
        keyedObjectJmx.clear();

        final Collection<org.glassfish.grizzly.memcached.pool.BaseObjectPool.QueuePool<K, V>> keyedObjects = pool.getValues();
        keyedObjects.forEach(keyedObj -> {
            final Object jmxObj = keyedObj.getMonitoringConfig().createManagementObject();
            mom.register(this, jmxObj);
            keyedObjectJmx.add(jmxObj);
        });
    }

    @ManagedData(name = "Object Pool Stat")
    private static class CompositeObjectPoolStat {
        @ManagedAttribute(id = "total-pools")
        @Description("The total number of instances for all keys managed by this object pool.")
        private final int totalPoolSize;

        @ManagedAttribute(id = "highest-peak")
        @Description("The highest peak number of instances among all keys managed by this object pool.")
        private final int highestPeakCount;

        @ManagedAttribute(id = "total-active")
        @Description(
                "The total number of instances currently borrowed from but not yet returned to the pool for all keys managed by this object pool.")
        private final int totalActiveCount;

        @ManagedAttribute(id = "total-idle")
        @Description("The total number of instances currently idle for all keys managed by this object pool.")
        private final int totalIdleCount;

        private CompositeObjectPoolStat(int totalPoolSize, int highestPeakCount, int totalActiveCount,
                                        int totalIdleCount) {
            this.totalPoolSize = totalPoolSize;
            this.highestPeakCount = highestPeakCount;
            this.totalActiveCount = totalActiveCount;
            this.totalIdleCount = totalIdleCount;
        }
    }
}
