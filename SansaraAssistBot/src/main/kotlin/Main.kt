import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.time.LocalDate
import kotlin.random.Random

val daysToNewYear = LocalDate.of(2022, 12, 31).dayOfYear - LocalDate.now().dayOfYear

fun main() {
    val botsApi = TelegramBotsApi(DefaultBotSession()::class.java)
    botsApi.registerBot(SansaraAssistBot())
}

class SansaraAssistBot : TelegramLongPollingBot() {
    override fun getBotToken() = "5629592284:AAGZwTH6wRlFm8UxtiLOLkab-ozCmBx7IYs"

    override fun getBotUsername() = "SansaraAssistBot"

    override fun onUpdateReceived(update: Update?) {
        val base: Array<Triple<String, Int, Int>> = arrayOf(
            Triple("Geeronimo", 29, 0),
            Triple("АнтонГурьянов", 25, 0),
            Triple("РоманЛюбушин", 25, 0),
            Triple("Nazar", 24, 0),
            Triple("korgelie", 24, 0),
            Triple("АлександрАтр", 22, 0),
            Triple("Дюк", 20, 0),
            Triple("Kapa6ac18", 20, 0),
            Triple("godhedin", 20, 0),
            Triple("SergeyBurenkov", 20, 0),
            Triple("Olegvodeniktov", 20, 0),
            Triple("seryibelyi", 20, 0)
        )

        val text = update!!.message.text.filterNot { it == ' ' || it == '—' }

        if (text.contains("Топ-10пидоров")) {
            val listString = text.lines().filter { it.contains(Regex("""^\d""")) }
            val onlyNameAndCount = listString.map { it.dropLast(6).drop(2).replace(".", "") }

            base.forEach { pidor ->
                onlyNameAndCount.forEach { string ->
                    if (string.contains(Regex(pidor.first))) {
                        val onlyCount = string.replace(pidor.first, "")
                        val newCount = pidor.second + onlyCount.toInt()
                        val i = base.indexOfFirst { pidor.first == it.first }
                        base[i] = Triple(pidor.first, newCount, pidor.third)
                    }
                }
            }

            val newBase = getBaseWithPercent(base)
                .sortedBy { it.second }
                .reversed()

            val stringBuilding = StringBuilder()
            stringBuilding.append("Осталось $daysToNewYear дней до НГ, шансы на успех:\n\n")
            var i = 1
            newBase.forEach {
                stringBuilding.append("${i++}. ${it.first} — ${it.second}   ${(it.third).toDouble()/10000} %\n")
            }

            val messageText = stringBuilding.toString()
            val message = SendMessage()
            message.setChatId(update.message.chatId)
            message.text = messageText
            execute(message)
        }
    }

    private fun getBaseWithPercent(base: Array<Triple<String, Int, Int>>): Array<Triple<String, Int, Int>> {

        val baseWithPercent = base.clone()

        repeat(1000000) {
            val winnerOfYear = getWinnerOfYear(base)
            val i = baseWithPercent.indexOfFirst { it.first == winnerOfYear}
            baseWithPercent[i] = Triple(baseWithPercent[i].first, baseWithPercent[i].second, baseWithPercent[i].third + 1)
        }
        return baseWithPercent
    }

    private fun getWinnerOfYear(base: Array<Triple<String, Int, Int>>): String {

        val baseClone = base.clone()

        repeat(daysToNewYear) {
            val x = Random.nextInt(0, baseClone.lastIndex+1)
            baseClone[x] = Triple(baseClone[x].first, baseClone[x].second + 1, baseClone[x].third)
        }

        baseClone.sortBy { it.second }

        val p1 = baseClone.last()
        val p2 = baseClone[baseClone.size-2]
        if (p1.second == p2.second && Random.nextBoolean()) return p2.first
        return p1.first
    }
}