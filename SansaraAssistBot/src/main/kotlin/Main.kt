import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

fun main() {
    val botsApi = TelegramBotsApi(DefaultBotSession()::class.java)
    botsApi.registerBot(SansaraAssistBot())
}

class SansaraAssistBot : TelegramLongPollingBot() {
    override fun getBotToken() = "5629592284:AAGZwTH6wRlFm8UxtiLOLkab-ozCmBx7IYs"

    override fun getBotUsername() = "SansaraAssistBot"

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

    override fun onUpdateReceived(update: Update) {
        val text = update.message.text.filterNot { it == ' '|| it == '—' }
        val listString = text.lines().filter { it.contains(Regex("""^\d""")) }
        val onlyNameAndCount = listString.map { it.dropLast(6).drop(2).replace(".", "") }

        //val new2 = newList.map { "%s".format(it) }
        //var formatTemplate = "%-2s\t%s"

        //it.split(" - ")


        var messageText = ""
        //messageText = listString.toString()
        messageText = onlyNameAndCount.toString()

        //val array = text.split('\n')

        /*val reg = "\n"
        val listString: MutableList<String> = mutableListOf()
        val foundString = reg.toRegex().findAll(text)
for (item in foundString) {
listString.add(item.groupValues[1])}*/
        //messageText = listString[0]

        if (text == "/start") {
            val message = SendMessage()
            message.setChatId(update.message.chatId)
            message.text = update.message.messageId.toString()
            execute(message)
        }
        val message = SendMessage()
        message.setChatId(update.message.chatId)
        message.text = messageText
        execute(message)
    }
}
//val reg = “.+ - (\d)\n”
//val listNum: MutableList<Int> = mutableListOf()
//val foundNum = reg.toRegex.findAll(string)
//for (item in foundNum) {
//listNum.add(item.groupValues[1].toInt)}


//string.split("-")[1].toInt()
//pidorstats@SublimeBot
/*
Топ-10 пидоров за текущий год:

1. Geeronimo — 29 раз(а)
2. Роман Любушин — 25 раз(а)
3. Антон Гурьянов — 25 раз(а)
4. Nazar — 24 раз(а)
5. korgelie — 24 раз(а)
6. Александр Атр — 22 раз(а)
7. Дюк — 21 раз(а)
8. Kapa6ac18 — 20 раз(а)
9. godhedin — 20 раз(а)
10. SergeyBurenkov — 20 раз(а)

Всего участников — 12
*/
//if (inputText.contains(Regex("""[^-\w]|\d|^-|-$|_""")) || inputText.isEmpty()) toast()
//            else viewModel.onAddBtn(inputText)

enum class Pidor(name: String) {
    GEERONIMO("Geeronimo"),
    ANTON("Антон Гурьянов"),
    PAKER("Iam32go"),
    NAZAR("Nazar"),
    KORZH("korgelie"),
    ATR("Aleksandr_Atr"),
    DUKE("Дюк"),
    KARABAS("Kapa6ac18"),
    PARAMON("godhedin"),
    BUR("SergeyBurenkov"),
    OLEG("Olegvodeniktov"),
    BELOV("seryibelyi")
}