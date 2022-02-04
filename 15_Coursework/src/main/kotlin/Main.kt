import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

@kotlinx.coroutines.ExperimentalCoroutinesApi
suspend fun main() {
    coroutineScope {
        val distributionCenter = DistributionCenter(14, 26)
        val channelArrivingTrucks = produceArrivingTrucks()

        launch { distributionCenter.unloadingPort(channelArrivingTrucks) }
        launch { distributionCenter.loadingPort() }

        delay(53000)
        channelArrivingTrucks.cancel()
        println("Рабочий день и прием машин на разгрузку заканчивается")
        delay(7000)
        println("Рабочий день закончен")
        coroutineContext.cancelChildren()
    }
}

@kotlinx.coroutines.ExperimentalCoroutinesApi
fun CoroutineScope.produceArrivingTrucks() = produce {
    while (true) {
        send(createArrivingTruck())
    }
}