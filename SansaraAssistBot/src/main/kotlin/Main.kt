import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

fun main() {
    try {
        val botsApi = TelegramBotsApi(DefaultBotSession()::class.java)
        botsApi.registerBot(SansaraAssistBot())
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}

class SansaraAssistBot : TelegramLongPollingBot() {
    override fun getBotToken() = "5629592284:AAGZwTH6wRlFm8UxtiLOLkab-ozCmBx7IYs"

    override fun getBotUsername() = "SansaraAssistBot"

    override fun onUpdateReceived(update: Update) {
        if (update.message.text == "/start") {
            val message = SendMessage()
            message.setChatId(update.message.chatId)
            message.text = update.message.messageId.toString()
            execute(message)
        }
        if (update.message.text == "60") {
            val message = SendMessage()
            message.setChatId(update.message.chatId)
            message.text = "привет, разведка!"
            execute(message)
        }
        val message = SendMessage()
        message.setChatId(update.message.chatId)
        message.text = update.message.date.toString()
        execute(message)
    }
}
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