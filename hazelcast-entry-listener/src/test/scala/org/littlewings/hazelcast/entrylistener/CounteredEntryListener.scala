package org.littlewings.hazelcast.entrylistener

import com.hazelcast.core.{EntryEvent, EntryListener}

class CounteredEntryListener extends EntryListener[String, String] {
  var added: Int = _
  var updated: Int = _
  var removed: Int = _
  var evicted: Int = _

  override def entryAdded(event: EntryEvent[String, String]): Unit =
    added += 1

  override def entryUpdated(event: EntryEvent[String, String]): Unit =
    updated += 1

  override def entryRemoved(event: EntryEvent[String, String]): Unit =
    removed += 1

  override def entryEvicted(event: EntryEvent[String, String]): Unit =
    evicted += 1
}
