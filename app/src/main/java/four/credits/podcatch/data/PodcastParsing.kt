package four.credits.podcatch.data

import android.util.Xml
import four.credits.podcatch.domain.Episode
import four.credits.podcatch.domain.Podcast
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.lang.IllegalStateException

// TODO: don't throw in this code
val ns: String? = null

fun parsePodcast(stream: InputStream, link: String) = Xml.newPullParser()
    .apply {
        setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        setInput(stream, null)
        nextTag()
    }
    .parsePodcasts(link)

fun XmlPullParser.parsePodcasts(link: String): List<Podcast> =
    withinTag("rss") {
        val entries = mutableListOf<Podcast>()
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) {
                continue
            } else if (name == "channel") {
                entries.add(readChannel(link))
            } else {
                skip()
            }
        }
        entries
    }

fun <T> XmlPullParser.withinTag(tag: String, action: (XmlPullParser) -> T): T {
    require(XmlPullParser.START_TAG, ns, tag)
    val result = action(this)
    require(XmlPullParser.END_TAG, ns, tag)
    return result
}

fun XmlPullParser.readChannel(link: String) = withinTag("channel") {
    var title: String? = null
    var description: String? = null
    val episodes = mutableListOf<Episode>()
    while (next() != XmlPullParser.END_TAG) {
        if (eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (name) {
            "title" -> title = readContents("title")
            "description" -> description = readContents("description")
            else -> skip()
        }
    }
    // TODO: populate episodes list
    if (title != null && description != null)
        Podcast(title, description, link, episodes)
    else throw ParseError(
        "one or more parameters are null: ($title, $description)"
    )
}

// read the contents of a tag with name `tag` under the cursor
fun XmlPullParser.readContents(tag: String): String = withinTag(tag) {
    var result = ""
    if (next() == XmlPullParser.TEXT) {
        result = text
        nextTag()
    }
    result
}

// skip the current tag. works even if the tag is recursive
fun XmlPullParser.skip() {
    if (eventType != XmlPullParser.START_TAG) {
        throw IllegalStateException()
    }
    var depth = 1
    while (depth != 0) {
        when (next()) {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}

class ParseError(message: String, cause: Throwable? = null) :
    Exception("Error during parsing: $message", cause)
