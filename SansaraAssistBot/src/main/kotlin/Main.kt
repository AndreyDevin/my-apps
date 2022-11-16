import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.time.LocalDate
import kotlin.random.Random

val daysToNewYear get() = LocalDate.of(2022, 12, 31).dayOfYear - LocalDate.now().dayOfYear

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
            Triple("Iam32go", 25, 0),
            Triple("Nazar", 24, 0),
            Triple("korgelie", 24, 0),
            Triple("Aleksandr_Atr", 22, 0),
            Triple("Дюк", 20, 0),
            Triple("Kapa6ac18", 20, 0),
            Triple("godhedin", 20, 0),
            Triple("SergeyBurenkov", 20, 0),
            Triple("Olegvodeniktov", 20, 0),
            Triple("seryibelyi", 20, 0)
        )

        val text = update!!.message.text

        if (text.contains("Топ")) {
            val onlyNameAndCount = text.lines().map {
                //it.replace(Regex("""^\d+|(раз\(а\))| |—|\."""), "")
                it.replace(Regex("""^\d+|[ —.]|(раз\(а\))"""), "")//то же самое, что и выше
            }

            val missingNewDataList: MutableList<String> = base.map { it.first } as MutableList<String>

            base.forEach { triple ->
                onlyNameAndCount.forEach { string ->
                    if (string.contains(triple.first)) {
                        val onlyCount = string.replace(triple.first, "")
                        val newCount = triple.second + onlyCount.toInt()
                        val i = base.indexOfFirst { triple.first == it.first }
                        base[i] = Triple(triple.first, newCount, triple.third)

                        missingNewDataList.remove(triple.first)
                    }
                }
            }

            val newBase = getBaseWithPercent(base)
                .sortedBy { it.second }
                .reversed()

            val messageStringBuilding = StringBuilder()
            messageStringBuilding.append("Осталось $daysToNewYear дней до НГ, шансы на успех:\n\n")
            var i = 1
            newBase.forEach {
                if (it.first == "Iam32go") messageStringBuilding.append("${i++}. Красавчик Пак — ${it.second}   ${(it.third).toDouble() / 1000} %\n")
                else messageStringBuilding.append("${i++}. ${it.first} — ${it.second}   ${(it.third).toDouble() / 1000} %\n")
            }
            if (missingNewDataList.isNotEmpty())
                messageStringBuilding.append("\n!!!critical extreme attention!!!\nданные не обновлены для:\n$missingNewDataList")

            val message = SendMessage()
            message.setChatId(update.message.chatId)
            message.text = messageStringBuilding.toString()
            execute(message)
        }
    }

    private fun getBaseWithPercent(base: Array<Triple<String, Int, Int>>): Array<Triple<String, Int, Int>> {

        val baseWithPercent = base.clone()

        repeat(100000) {
            val winnerOfYear = getWinnerOfYear(base)
            val i = baseWithPercent.indexOfFirst { it.first == winnerOfYear }
            baseWithPercent[i] =
                Triple(baseWithPercent[i].first, baseWithPercent[i].second, baseWithPercent[i].third + 1)
        }
        return baseWithPercent
    }

    private fun getWinnerOfYear(base: Array<Triple<String, Int, Int>>): String {

        val baseClone = base.clone()

        repeat(daysToNewYear) {
            val x = Random.nextInt(0, baseClone.lastIndex + 1)
            baseClone[x] = Triple(baseClone[x].first, baseClone[x].second + 1, baseClone[x].third)
        }

        baseClone.sortBy { it.second }
        val winners = baseClone.filter { it.second == baseClone.last().second }
        return winners.random().first
    }
}
//stringBuilding.append("\nmessage.from: ${update.message.from}\n\nme: ${me}\n\n${update}")