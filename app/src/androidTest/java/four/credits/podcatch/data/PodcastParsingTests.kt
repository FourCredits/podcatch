package four.credits.podcatch.data

import four.credits.podcatch.domain.Podcast
import org.junit.Test
import org.junit.Assert.*

class PodcastParsingTests {
    @Test
    fun correctlyParsesMultipleChannels() {
        val input = """
            <rss>
              <channel>
                <title>Podcast 1</title>
                <link>https://example.com/1</link>
                <description>Foo</description>
              </channel>
              <channel>
                <title>Podcast 2</title>
                <link>https://example.com/2</link>
                <description>Bar</description>
              </channel>
            </rss>
        """.trimIndent().byteInputStream()
        val result = parsePodcast(input, "https://example.com/top-level")
        // TODO: make tests for feeds with non-zero episodes
        // TODO: make tests for feeds with only one channel
        val expected = listOf(
            Podcast("Podcast 1", "Foo", "https://example.com/top-level", listOf()),
            Podcast("Podcast 2", "Bar", "https://example.com/top-level", listOf())
        )
        assertEquals(expected, result)
    }
}
