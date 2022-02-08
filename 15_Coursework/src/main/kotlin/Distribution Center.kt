import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DistributionCenter(
    private val numberOfUnloadingPorts: Int,
    private val numberOfLoadingPorts: Int
) {
    private val unloadingPorts =
        MutableList<Truck?>(numberOfUnloadingPorts) { null }//Это список разгрузочных портов. Во время работы программы, в каждом порте находится или машина или null.
    private val loadingPorts =
        MutableList<Truck?>(numberOfLoadingPorts) { null }//Это список загрузочных портов. В каждом порте находится или машина или null.
    private val storageArea = mutableListOf<Product>()//Это хранилище всех товаров.
    private val mutex = Mutex()

    //fun printStageInfo печатает всю информацию о событиях в DistributionCenter. Событием считается приезд машины к DistributionCenter или отъезд.
    //Например, сообщение "loading-16 --> N866 cargo: 25 x Laptop = 5000kg" следует читать как "от шестнадцатого загрузочного порта
    //отъехала машина с номерами 866, с грузом 25 упаковок ноутбуков, общий вес груза 5000кг"
    //А сообщение "loading-16 <-- N181 maxWeight-10000kg" означает, "к шестнадцатому порту подъехала на загрузку пустая машина N181, максимальной грузоподъемностью 10т"
    //"N677 --> unloadingPort-7 maxWeight-10000kg cargoWeight-9900kg" - ...на разгрузку подъехала N677, максимальной грузоподъемностью 10т, общий вес груза 9,9т
    //"N677 <-- unloadingPort-7" - пустая, разгруженная N677 отъехала от разгрузочного порта 7.
    //После сообщения о событии, в этой же строке, функция печатает информацию о том какие машины стоят во всех портах
    //и вес груза на данный конкретный момент внутри этих машин.
    //Завершается информационная строка, выводом сколько и чего находится на складе.
    private suspend fun printStageInfo( message: String ) {
        mutex.withLock {
            val loadingPortInfo = StringBuilder()//в этой переменной формируется информация о состоянии загрузочных портов
            loadingPorts.forEach { inPort ->
                if (inPort == null)
                    loadingPortInfo.append("_________ ")
                else loadingPortInfo.append("N${inPort.carNumber}-${inPort.cargo.sumOf { it.weight }} ")
            }

            val unloadingPortInfo = StringBuilder()//в этой переменной формируется информация о состоянии разгрузочных портов
            unloadingPorts.forEach { inPort ->
                if (inPort == null)
                    unloadingPortInfo.append("_________ ")
                else unloadingPortInfo.append("N${inPort.carNumber}-${inPort.cargo.sumOf { it.weight }} ")
            }

            print(
                "$message ${requiredNumberOfSpaces(65, message.length)} Unload Ports: $unloadingPortInfo " +
                        "${requiredNumberOfSpaces(11 * numberOfUnloadingPorts, unloadingPortInfo.length)} " +
                        "Load Ports: $loadingPortInfo ${requiredNumberOfSpaces(10 * numberOfLoadingPorts, loadingPortInfo.length )}"
            )

            val productMap = storageArea.groupBy { it.name }
            print(" In stock: ")
            productMap.forEach { print("${it.key}-${it.value.size} ") }
            println()
        }
    }

    // fun requiredNumberOfSpaces добавляет нужное количество пробелов в конец сообщения, чтобы информация из fun printStageInfo выводилась ровными колонками
    private fun requiredNumberOfSpaces(maxLength: Int, messageLength: Int): String {
        val requiredNumberOfSpaces = StringBuilder()
        repeat(maxLength - messageLength) { requiredNumberOfSpaces.append(" ") }
        return requiredNumberOfSpaces.toString()
    }

    //функция берет со склада необходимый для погрузки продукт
    private suspend fun getProductFromStorage(rightProduct: Product?): Product? {
        mutex.withLock {
            if (rightProduct in storageArea) {
                storageArea.remove(rightProduct)
                return rightProduct
            }
            return null
        }
    }

    //функция разгрузки канала грузовиков, который приходит из fun main
    //Каждый порт разгрузки работает в своей корутине, изначально в него помещен null
    suspend fun unloadingPort(trucksChannel: ReceiveChannel<Truck>) = coroutineScope {
        repeat(numberOfUnloadingPorts) {
            launch {
                for (arrivingTruck in trucksChannel) {

                    delay(arrivingTruck.parkingTime)
                    unloadingPorts[it] = arrivingTruck//В порт встал грузовик.
                    //Информация об этом событии будут выведена в консоль, вместе с другой информацией о происходящем в DistributionCenter
                    printStageInfo(
                        "N${arrivingTruck.carNumber} --> unloadingPort-${it + 1} " +
                        "maxWeight-${arrivingTruck.maxLoadCapacity}kg cargoWeight-${arrivingTruck.cargo.sumOf { it.weight }}kg"
                    )

                    for (i in arrivingTruck.cargo.size - 1 downTo 0) {
                        delay(arrivingTruck.cargo[i].loadingTime)
                        mutex.withLock { storageArea.add(arrivingTruck.cargo[i]) }
                        arrivingTruck.cargo.remove(arrivingTruck.cargo[i])
                    }
                    //грузовик разгружен, в порт снова помещается null, информация о событии выводится в консоль.
                    unloadingPorts[it] = null
                    printStageInfo("N${arrivingTruck.carNumber} <-- unloadingPort-${it + 1}")
                }
            }
        }
    }

    //Функция портов загрузки. Тот же принцип: каждый порт в своей корутине, изначально в него помещен null.
    suspend fun loadingPort() {
        coroutineScope {
            repeat(numberOfLoadingPorts) {
                launch {
                    while (isActive) {
                        val loadingTruck = createLoadingTruck()
                        delay(loadingTruck.parkingTime)
                        loadingPorts[it] = loadingTruck

                        printStageInfo("loading-${it + 1} <-- N${loadingTruck.carNumber} maxWeight-${loadingTruck.maxLoadCapacity}kg")

                        loadingTruck.cargo = createLoadingTruckCargo(loadingTruck)//грузовик получает груз в отдельной функции

                        loadingPorts[it] = null
                        val message = "loading-${it + 1} --> N${loadingTruck.carNumber} cargo: ${loadingTruck.cargo.size}" +
                                    " x ${loadingTruck.cargo.first().name} = ${loadingTruck.cargo.sumOf { it.weight }}kg"
                        printStageInfo(message)
                    }
                }
            }
        }
    }

    private suspend fun createLoadingTruckCargo(truck: Truck): MutableList<Product> {
        val randomProduct: Product = ProductList.productList[(0 until ProductList.productList.size).random()]
        var firstRandomCargo: Product? = null
        while (firstRandomCargo == null) {
            firstRandomCargo = getProductFromStorage(randomProduct) //пытаемся получить со склада первый случайный товар
            delay(100) //если этого товара пока нет на складе, ждем 0,1 сек
        }
        truck.cargo.add(firstRandomCargo)
        delay(truck.cargo.first().loadingTime)
//загрузили первый товар, далее будем заполнять грузовик такими же товарами
        var totalCargoWeight = truck.cargo.first().weight
        while (truck.maxLoadCapacity > totalCargoWeight) {
            val nextProduct = getProductFromStorage(truck.cargo.first())
            if (nextProduct != null) {
                if (truck.maxLoadCapacity < totalCargoWeight + nextProduct.weight) break

                totalCargoWeight += nextProduct.weight
                truck.cargo.add(nextProduct)
                delay(nextProduct.loadingTime)
            } else delay(1000) //если товар закончился на складе, ждем 1 сек, может подвезут...
        }
        return truck.cargo
    }
}
