// Execution Command: $ groovy hazelcast-getting-started.groovy

@Grab('com.hazelcast:hazelcast:3.2')
import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast

def cfg = new Config()
def instance = Hazelcast.newHazelcastInstance(cfg)

def mapCustomers = instance.getMap("customers")

[[1, "Joe"], [2, "Ali"], [3, "Avi"]].each { i, name ->
    mapCustomers.put(i, name)
}

println("Customer with key 1: ${mapCustomers.get(1)}")
println("Map Size: ${mapCustomers.size()}")

def queueCustomers = instance.getQueue("customers")
["Tom", "Mary", "Jane"].each { queueCustomers.offer(it) }

println("First Customer: ${queueCustomers.poll()}")
println("Second Customer: ${queueCustomers.peek()}")
println("Queue size: ${queueCustomers.size()}")

instance.lifecycleService.shutdown()
