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
        Pidor.PAKER to 24,
        Pidor.NAZAR to 23,
        Pidor.KORZH to 23,
        Pidor.ATR to 22,
        Pidor.DUKE to 20,
        Pidor.KARABAS to 19,
        Pidor.PARAMON to 19,
        Pidor.BUR to 19,
        Pidor.OLEG to 18,
        Pidor.BELOV to 18
    )

    override fun onUpdateReceived(update: Update) {
        val text = update.message.text
        val listString = text.lines().filter { it.contains(Regex("""^\d""")) }

        var messageText = ""
        messageText = listString.toString()

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

1. seryibelyi — 6 раз(а)
2. Дюк — 2 раз(а)
3. SergeyBurenkov — 2 раз(а)
4. Geeronimo — 2 раз(а)
5. Olegvodeniktov — 2 раз(а)
6. Nazar — 2 раз(а)
7. korgelie — 2 раз(а)
8. Антон Гурьянов — 1 раз(а)
9. Kapa6ac18 — 1 раз(а)

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