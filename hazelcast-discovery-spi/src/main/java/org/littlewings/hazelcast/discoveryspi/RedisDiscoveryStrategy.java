package org.littlewings.hazelcast.discoveryspi;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import reactor.core.publisher.Flux;

public class RedisDiscoveryStrategy extends AbstractDiscoveryStrategy {
    DiscoveryNode discoveryNode;
    RedisClient redisClient;
    StatefulRedisConnection<String, String> connection;

    String redisUrl;

    public RedisDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
        super(logger, properties);

        this.discoveryNode = discoveryNode;

        redisUrl = getOrDefault(
                "redis.discovery",
                RedisDiscoveryConfiguration.REDIS_URL,
                "redis://redispass@172.17.0.2:6379/0"
        );
    }

    @Override
    public void start() {
        redisClient = RedisClient.create(redisUrl);
        connection = redisClient.connect();

        if (discoveryNode != null) {
            RedisReactiveCommands<String, String> commands = connection.reactive();
            commands.sadd("hazelcast-nodes", getLocalNodeAddress()).block();

            getLogger().info("register local-node[" + getLocalNodeAddress() + "]");
        }
    }

    String getLocalNodeAddress() {
        return discoveryNode.getPrivateAddress().getHost() + ":" + discoveryNode.getPrivateAddress().getPort();
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        RedisReactiveCommands<String, String> commands = connection.reactive();

        Flux<String> nodes = commands.smembers("hazelcast-nodes");

        getLogger().info("discovery nodes");

        return nodes
                .map(n -> {
                            getLogger().info("node: " + n);

                            String host = n.split(":")[0];
                            int port = Integer.parseInt(n.split(":")[1]);

                            try {
                                Map<String, Object> attributes = new HashMap<>();
                                attributes.put("url", redisUrl);
                                attributes.put("host", host);
                                attributes.put("port", port);

                                Address address = new Address(InetAddress.getByName(host), port);
                                return (DiscoveryNode) new SimpleDiscoveryNode(address, attributes);
                            } catch (UnknownHostException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .collectList()
                .block();
    }

    @Override
    public void destroy() {
        getLogger().info("shutdown redis discovery strategy...");

        try {
            if (discoveryNode != null) {
                RedisReactiveCommands<String, String> commands = connection.reactive();
                commands.srem("hazelcast-nodes", getLocalNodeAddress()).block();
            }
        } finally {
            connection.close();
            redisClient.shutdown();
        }
    }
}
