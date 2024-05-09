package four.credits.podcatch.data

import four.credits.podcatch.domain.Episode
import four.credits.podcatch.domain.Podcast
import org.junit.Test
import org.junit.Assert.*

class PodcastParsingTests {
    @Test
    fun correctlyParsesSingleChannel() {
        val input = """
            <rss>
              <channel>
                <title>Podcast 1</title>
                <link>https://example.com/1</link>
                <description>Foo</description>
              </channel>
            </rss>
        """.trimIndent().byteInputStream()
        val result = parsePodcast(input, "https://example.com/top-level")
        val expected = listOf(
            Podcast("Podcast 1", "Foo", "https://example.com/top-level", listOf()),
        )
        assertEquals(expected, result)
    }

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
        val expected = listOf(
            Podcast(
                "Podcast 1",
                "Foo",
                "https://example.com/top-level",
                listOf()
            ),
            Podcast(
                "Podcast 2",
                "Bar",
                "https://example.com/top-level",
                listOf()
            )
        )
        assertEquals(expected, result)
    }

    @Test
    fun correctlyParsesChannelWithEpisodes() {
        val input = """
            <rss>
              <channel>
                <title>Podcast 1</title>
                <link>https://example.com/1</link>
                <description>Foo</description>
                <item>
                  <title>Episode 1</title>
                  <description>the description of the episode</description>
                  <enclosure
                    type="audio/mpeg"
                    length="0"
                    url="https://example.com/1/1" />
                </item>
                <item>
                  <title>Episode 2</title>
                  <link>https://example.com/1/2</link>
                  <description>the second description</description>
                  <enclosure
                    type="audio/mpeg"
                    length="0"
                    url="https://example.com/1/2" />
                </item>
              </channel>
            </rss>
        """.trimIndent().byteInputStream()
        val result = parsePodcast(input, "https://example.com/top-level")
        val expected = listOf(
            Podcast(
                "Podcast 1",
                "Foo",
                "https://example.com/top-level",
                listOf(
                    Episode(
                        title = "Episode 1",
                        link = "https://example.com/1/1",
                        description = "the description of the episode",
                    ),
                    Episode(
                        title = "Episode 2",
                        link = "https://example.com/1/2",
                        description = "the second description",
                    )
                )
            ),
        )
        assertEquals(expected, result)
    }
}
