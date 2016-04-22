@Grab('com.hazelcast:hazelcast:3.6.2')
import com.hazelcast.core.Hazelcast

def hazelcast = Hazelcast.newHazelcastInstance()

System.console().readLine("> Enter, stop server.")
