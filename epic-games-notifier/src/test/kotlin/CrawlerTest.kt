import dev.schlaubi.epic_games_notifier.HttpRequests
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class CrawlerTest {

    @Test
    fun `test games are found`() = runTest {
        assert(HttpRequests.fetchFreeGames("de").isNotEmpty())
    }
}