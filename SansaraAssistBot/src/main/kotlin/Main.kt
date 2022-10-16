import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

enum class Pidor(val _name: String) {
    GEERONIMO("Geeronimo"),
    ANTON("АнтонГурьянов"),
    PAKER("РоманЛюбушин"),
    NAZAR("Nazar"),
    KORZH("korgelie"),
    ATR("АлександрАтр"),
    DUKE("Дюк"),
    KARABAS("Kapa6ac18"),
    PARAMON("godhedin"),
    BUR("SergeyBurenkov"),
    OLEG("Olegvodeniktov"),
    BELOV("seryibelyi")
}

fun main() {
    val botsApi = TelegramBotsApi(DefaultBotSession()::class.java)
    botsApi.registerBot(SansaraAssistBot())
}

class SansaraAssistBot : TelegramLongPollingBot() {
    override fun getBotToken() = "5629592284:AAGZwTH6wRlFm8UxtiLOLkab-ozCmBx7IYs"

    override fun getBotUsername() = "SansaraAssistBot"

    override fun onUpdateReceived(update: Update) {

        val pidorOfDay: MutableMap<Pidor, Int> = mutableMapOf(
            Pidor.GEERONIMO to 29,
            Pidor.ANTON to 25,
            Pidor.PAKER to 25,
            Pidor.NAZAR to 24,
            Pidor.KORZH to 24,
            Pidor.ATR to 22,
            Pidor.DUKE to 21,
            Pidor.KARABAS to 20,
            Pidor.PARAMON to 20,
            Pidor.BUR to 20,
            Pidor.OLEG to 19,
            Pidor.BELOV to 19
        )

        val text = update.message.text.filterNot { it == ' '|| it == '—' }
        val listString = text.lines().filter { it.contains(Regex("""^\d""")) }
        val onlyNameAndCount = listString.map { it.dropLast(6).drop(2).replace(".", "") }

        pidorOfDay.keys.forEach { pidor ->
            onlyNameAndCount.forEach {
                if (it.contains(Regex(pidor._name))) {
                    val onlyCount = it.replace(pidor._name, "")
                    pidorOfDay[pidor] = pidorOfDay[pidor]!! + onlyCount.toInt()
                }
            }
        }

        val sortedList = pidorOfDay.toList()
            .sortedBy { (key, value) -> value }
            .reversed()
            .toMap()

        val stringBuilding = StringBuilder()
        stringBuilding.append("Топ-10 пидоров за текущий год:\n\n")
        var i = 1
        sortedList.forEach { (key, value) -> stringBuilding.append("${i++}. $key — $value\n")}
        stringBuilding.append("\n\nВсего участников — ${pidorOfDay.size}")

        val messageText = stringBuilding.toString()
        val message = SendMessage()
        message.setChatId(update.message.chatId)
        message.text = messageText
        execute(message)
    }
}