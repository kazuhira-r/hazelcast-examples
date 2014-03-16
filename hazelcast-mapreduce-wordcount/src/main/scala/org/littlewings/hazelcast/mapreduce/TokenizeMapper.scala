package org.littlewings.hazelcast.mapreduce

import scala.util.matching.Regex

import com.hazelcast.mapreduce.{Context, Mapper}
import org.apache.lucene.analysis.ja.JapaneseAnalyzer
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.util.Version

object TokenizeMapper {
  private val NOMINAL_REGEX: Regex = "名詞".r

  def isNominal(source: String): Boolean =
    NOMINAL_REGEX.findFirstIn(source).isDefined
}

class TokenizeMapper extends Mapper[String, String, String, Long] {
  override def map(key: String, value: String, context: Context[String, Long]): Unit = {
    val analyzer = new JapaneseAnalyzer(Version.LUCENE_47)
    val tokenStream = analyzer.tokenStream("", value)
    val charTermAttr = tokenStream.addAttribute(classOf[CharTermAttribute])
    val partOfSpeechAttr = tokenStream.addAttribute(classOf[PartOfSpeechAttribute])

    tokenStream.reset()

    try {
      Iterator
        .continually(tokenStream.incrementToken())
        .takeWhile(_ == true)
        .withFilter(b => TokenizeMapper.isNominal(partOfSpeechAttr.getPartOfSpeech))
        .map(b => charTermAttr.toString)
        .foreach(token => context.emit(token, 1L))
    } finally {
      tokenStream.end()
    }
  }
}
