import java.time.LocalDate
import kotlin.random.Random

val daysToNewYear = LocalDate.of(2022, 12, 31).dayOfYear - LocalDate.now().dayOfYear

fun main() {
    val testSansaraAssistBot = TestSansaraAssistBot()
    val update =
"""Топ-10 пидоров за текущий год:

1. seryibelyi — 6 раз(а)
2. Дюк — 4 раз(а)
3. Geeronimo — 3 раз(а)
4. korgelie — 3 раз(а)
5. SergeyBurenkov — 2 раз(а)
6. Olegvodeniktov — 2 раз(а)
7. Nazar — 2 раз(а)
8. Антон Гурьянов — 1 раз(а)
9. Kapa6ac18 — 1 раз(а)

Всего участников — 12"""
    testSansaraAssistBot.onUpdateReceived(update)
    testSansaraAssistBot.onUpdateReceived(update)
}

class TestSansaraAssistBot {

    fun onUpdateReceived(update: String) {
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

        val text = update.filterNot { it == ' ' || it == '—' }

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

            val newBase = getPercent(base)

            val sortedList = newBase
                .sortedBy { it.second }
                .reversed()
            //.toMap()

            val stringBuilding = StringBuilder()
            stringBuilding.append("Топ-10 пидоров за текущий год:\n\n")
            var i = 1
            sortedList.forEach { stringBuilding.append("${i++}. ${it.first} — ${it.second}  (${it.third})\n") }
            stringBuilding.append("\nВсего участников — ${Pidor.values().size}")

            println(stringBuilding)
        }
    }

    private fun getPercent(base: Array<Triple<String, Int, Int>>): Array<Triple<String, Int, Int>> {

        val winnerOfYearBase = base.clone()

        repeat(1000000) {
            val pidorOfYear = getPidorOfYear(base)
            val i = winnerOfYearBase.indexOfFirst { it.first == pidorOfYear}
            winnerOfYearBase[i] = Triple(winnerOfYearBase[i].first, winnerOfYearBase[i].second, winnerOfYearBase[i].third + 1)
        }
        return winnerOfYearBase
    }

    private fun getPidorOfYear(base: Array<Triple<String, Int, Int>>): String {

        val baseClone = base.clone()

        repeat(daysToNewYear) {
            val x = Random.nextInt(0, baseClone.lastIndex+1)
            baseClone[x] = Triple(baseClone[x].first, baseClone[x].second + 1, baseClone[x].third)
        }
        baseClone.sortBy { it.second }
        return baseClone.last().first
    }
}