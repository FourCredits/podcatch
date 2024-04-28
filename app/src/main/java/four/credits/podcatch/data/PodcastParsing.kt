package four.credits.podcatch.data

import android.util.Xml
import four.credits.podcatch.domain.Podcast
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.lang.IllegalStateException

// TODO: don't throw in this code
val ns: String? = null

fun parsePodcast(stream: InputStream): List<Podcast> {
    val parser: XmlPullParser = Xml.newPullParser()
    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
    parser.setInput(stream, null)
    parser.nextTag()

    // TODO: break what's below into its own function
    val entries = mutableListOf<Podcast>()
    parser.require(XmlPullParser.START_TAG, ns, "rss")
    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        if (parser.name == "channel") {
            entries.add(parser.readEntry())
        } else {
            parser.skip()
        }
    }
    parser.require(XmlPullParser.END_TAG, ns, "rss")
    return entries
}

fun XmlPullParser.readEntry(): Podcast {
    require(XmlPullParser.START_TAG, ns, "channel")
    var title: String? = null
    var description: String? = null
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
    require(XmlPullParser.END_TAG, ns, "channel")
    if (title != null && description != null)
        return Podcast(title, description)
    else
        throw ParseError(
            "one or more parameters are null: ($title, $description)"
        )
}

// read the contents of a tag with name `tag` under the cursor
fun XmlPullParser.readContents(tag: String): String {
    require(XmlPullParser.START_TAG, ns, tag)
    var result = ""
    if (next() == XmlPullParser.TEXT) {
        result = text
        nextTag()
    }
    require(XmlPullParser.END_TAG, ns, tag)
    return result
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

class ParseError(message: String, cause: Throwable? = null)
    : Exception("Error during parsing: $message", cause)
