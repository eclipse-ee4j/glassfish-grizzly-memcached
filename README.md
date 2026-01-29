# Grizzly Memcached

The Grizzly Memcached is a Java client library that allows programs to connect to, store data in, and retrieve data from a [Memcached](https://memcached.org/) server.

This client library handles the logic of routing requests to the appropriate server in a cluster, implementing features like consistent hashing, error handling, and failover.

It's built on top of [Grizzly NIO](https://github.com/eclipse-ee4j/glassfish-grizzly/).


## Key Functions

The Grizzly Memcached provides the interface for applications to interact with the distributed memory caching system.

Key features include:

- Connection Management: 
  - Establishing and maintaining connections to one or more Memcached servers.
- Key-Value Operations:
  - Performing various operations such as set, setMulti, get, getMulti, delete, deleteMulti, cas, increment, decrement and etc. with keys and raw data values.
  - Especially for multi-operations, it provides tremendous performance improvements for large amounts of data.
- Hashing and Routing:
  - Using a hashing algorithm (consistent hashing) to determine which server in a cluster a specific key belongs to, ensuring data can be found efficiently.
  - In particular, you can dynamically configure the configuration of servers through [Zookeeper](https://zookeeper.apache.org/).
- Error and Failure Handling:
  - Managing timeouts and network errors, and sometimes marking servers as dead and redirecting requests to the remaining active servers (failover).
  - It also supports automatic failback when the server comes back up.
- Protocol Support:
  - Communicating using the most efficient Memcached [Binary Protocol](https://docs.memcached.org/protocols/binary/).

## Getting Started

### Maven coordinates

```
<dependencies>
    <dependency>
        <groupId>org.glassfish.grizzly</groupId>
        <artifactId>grizzly-memcached</artifactId>
        <version>1.3.19</version>
    </dependency>
</dependencies>    
```

### Prerequisites

We have different JDK requirements depending on the branch in use:

- JDK 21+ for master and 1.4.x.
- JDK 1.8+ for 1.3.x.

Apache Maven 3.3.9 or later in order to build and run the tests.

### Installing and running the tests

If building in your local environment:

```
mvn clean install
```

### Example of use

```java
import org.glassfish.grizzly.memcached.GrizzlyMemcachedCache;
import org.glassfish.grizzly.memcached.GrizzlyMemcachedCacheManager;
import org.glassfish.grizzly.memcached.MemcachedCache;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;

public class Application {

    public static void main(String[] args) {
        // creates a CacheManager
        final GrizzlyMemcachedCacheManager manager = new GrizzlyMemcachedCacheManager.Builder().build();

        // creates a CacheBuilder
        final GrizzlyMemcachedCache.Builder<String, String> builder = manager.createCacheBuilder("USER");
      
        // sets initial servers
        final Set<SocketAddress> initServerSet = Set.of(new InetSocketAddress("memcached1.example.com", 11211),
                                                        new InetSocketAddress("memcached2.example.com", 11211));
        builder.servers(initServerSet);

        // creates the specific Cache
        final MemcachedCache<String, String> userCache = builder.build();

        // cache operations
        final int expirationTimeoutInSec = 60 * 30;
        boolean result = userCache.set("name", "foo", expirationTimeoutInSec, false);
        String value = userCache.get("name", false);
        // ...

        // shuts down
        manager.shutdown();
    }
}
```

### Performance Measurement

See the [benchmark results](https://github.com/eclipse-ee4j/glassfish-grizzly-memcached/wiki/Performance-Measurement).

## License

This project is licensed under the EPL-2.0 - see the [LICENSE.md](https://github.com/eclipse-ee4j/glassfish-grizzly-memcached/blob/master/LICENSE.md) file for details.
