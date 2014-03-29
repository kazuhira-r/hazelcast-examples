// Execution Command: $ sbt run

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast

object HazelcastGettingStarted {
  def main(args: Array[String]): Unit = {
    val cfg = new Config
    val instance = Hazelcast.newHazelcastInstance(cfg)

    val mapCustomers = instance.getMap[Int, String]("customers")

    for ((i, name) <- Array((1, "Joe"),
                            (2, "Ali"),
                            (3, "Avi"))) {
      mapCustomers.put(i, name)
    }

    println(s"Customer with key 1: ${mapCustomers.get(1)}")
    println(s"Map Size: ${mapCustomers.size}")


    val queueCustomers = instance.getQueue[String]("customers")

    Array("Tom", "Mary", "Jane").foreach(queueCustomers.offer)

    println(s"First Customer: ${queueCustomers.poll()}")
    println(s"Second Customer: ${queueCustomers.peek}")
    println(s"Queue size: ${queueCustomers.size}")

    instance.getLifecycleService.shutdown()
  }
}
