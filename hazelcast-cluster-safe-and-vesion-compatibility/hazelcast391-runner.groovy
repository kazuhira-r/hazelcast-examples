@Grab('com.hazelcast:hazelcast:3.9.1')
import com.hazelcast.core.Hazelcast

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.IntStream
import java.time.LocalDateTime

System.setProperty('hazelcast.jmx', 'true')
System.setProperty('hazelcast.http.healthcheck.enabled', 'true')

def log = { message ->
  println("[${LocalDateTime.now()}] ${message}")
}

log('starting Hazelcast Instance...')
def hazelcast = Hazelcast.newHazelcastInstance()

def running = new AtomicBoolean(true)

new Thread({ -> 
  while (running.get()) {
    def clusterState = hazelcast.cluster.clusterState
    def partitionService = hazelcast.partitionService

    log("Cluster#clusterState = ${clusterState}")
    log("PartitionService#clusterSafe = ${partitionService.clusterSafe}")

    // Reflection!!
    log("InternalPartitionServicem#migrationQueueSize = ${hazelcast.original.node.partitionService.migrationQueueSize}")

    TimeUnit.SECONDS.sleep(1L)
  }
}).start()

log('Hazelcast Instance started.')

System.console().readLine('> Enter input data')

def map = hazelcast.getMap('default')
IntStream.rangeClosed(1, 100000).forEach { i -> map.put("${hazelcast.cluster.localMember.uuid}-key${i}", "value${i}") }

log("putted data, size = ${map.size()}")

System.console().readLine('> Enter stop Hazelcast Instance')
running.set(false)

hazelcast.shutdown()

log('Hazelcast Instance shutdown.')
