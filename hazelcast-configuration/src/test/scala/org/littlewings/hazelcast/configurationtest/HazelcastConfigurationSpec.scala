package org.littlewings.hazelcast.configurationtest

import com.hazelcast.core.{Hazelcast, HazelcastInstance}
import com.hazelcast.config.ClasspathXmlConfig

import org.scalatest.{BeforeAndAfterAll,FunSpec}
import org.scalatest.Matchers._

class HazelcastConfigurationSpec extends FunSpec with BeforeAndAfterAll {
  // テストの開始から終了まで、浮いていてもらうサーバ
  val server: HazelcastInstance =
    Hazelcast
      .newHazelcastInstance(new ClasspathXmlConfig("hazelcast.xml"))

  describe("Hazelcast Configuration Spec") {
    it("cluster group spec") {
      withHazelcast { hazelcast =>
        hazelcast.getConfig.getGroupConfig.getName should be ("my-cluster")
        // 浮いているサーバが別個いるので、クラスタのメンバーは2になる
        hazelcast.getCluster.getMembers.size should be (2)
      }
    }

    it("ttl spec") {
      withHazelcast { hazelcast =>
        val map = hazelcast.getMap[String, String]("default")

        map.put("key1", "value1")

        // TTLを5秒に設定しているので、6秒待てば有効期限切れ
        map.get("key1") should be ("value1")
        Thread.sleep(6 * 1000L)
        map.get("key1") should be (null)

        // 他のIMapも、defaultの定義を引き継ぐ
        val otherMap = hazelcast.getMap[String, String]("other-map")

        otherMap.put("key1", "value1")

        otherMap.get("key1") should be ("value1")
        Thread.sleep(6 * 1000L)
        otherMap.get("key1") should be (null)
      }
    }

    it("double-backup-map spec") {
      // 明示的に、default以外のIMapの設定を定義した場合
      withHazelcast { hazelcast =>
        val config = hazelcast.getConfig

        config.getMapConfig("default").getBackupCount should be (1)
        config.getMapConfig("double-backup-map").getBackupCount should be (2)

        // 明示しなかった値は、MapConfigのデフォルト値が使用される模様
        config.getMapConfig("default").getTimeToLiveSeconds should be (5)
        config.getMapConfig("double-backup-map").getTimeToLiveSeconds should be (0)

        // 明示的な定義をしなかった場合は、「default」の内容で作成される模様
        config.getMapConfig("other-map-1").getTimeToLiveSeconds should be (5)
        config.getMapConfig("other-map-2").getTimeToLiveSeconds should be (5)
      }
    }

    it("wildcard-map spec") {
      withHazelcast { hazelcast =>
        val config = hazelcast.getConfig

        // ワイルドカードにマッチするものは、同じ定義を使用する
        config.getMapConfig("default").getMaxIdleSeconds should be (0)
        config.getMapConfig("wildcard-map-1").getMaxIdleSeconds should be (10)
        config.getMapConfig("wildcard-map-2").getMaxIdleSeconds should be (10)
      }
    }
  }

  def withHazelcast(fun: (HazelcastInstance => Unit)): Unit = {
    val hazelcast =
      Hazelcast
        .newHazelcastInstance(new ClasspathXmlConfig("hazelcast.xml"))

    try {
      fun(hazelcast)
    } finally {
        hazelcast.getLifecycleService.shutdown()
    }
  }

  override def afterAll: Unit = {
    // 全HazelcastInstanceのシャットダウン
    Hazelcast.shutdownAll()
  }
}
