class Truck(val maxLoadCapacity: Int, val parkingTime: Long) {
    val carNumber = (100..999).random()
    var cargo = mutableListOf<Product>()
}

fun createLoadingTruck(): Truck {
    var maxLoadCapacity = 10000
    var parkingTime: Long = 700
    when ((1..2).random()) {
        1 -> {
            maxLoadCapacity = 5000; parkingTime = 500
        }
    }
    return Truck(maxLoadCapacity, parkingTime)
}

fun createArrivingTruck(): Truck {
    var maxLoadCapacity = 20000
    var parkingTime: Long = 1000
    when ((1..3).random()) {
        1 -> { maxLoadCapacity = 5000; parkingTime = 500 }
        2 -> { maxLoadCapacity = 10000; parkingTime = 700 }
    }
    val truck = Truck(maxLoadCapacity, parkingTime)
    truck.cargo = createArrivingCargo(maxLoadCapacity)
    return truck
}

fun createArrivingCargo(maxLoadCapacity: Int): MutableList<Product> {
    val cargo = mutableListOf(ProductList.productList[(0 until ProductList.productList.size).random()])
    var totalCargoWeight = cargo.first().weight

    while (maxLoadCapacity - totalCargoWeight > 0) {
        val nextProduct = ProductList.productList[(0 until ProductList.productList.size).random()]
        if (nextProduct.food == cargo.first().food && nextProduct.breakingCargo == cargo.first().breakingCargo) {
                if (maxLoadCapacity - totalCargoWeight - nextProduct.weight < 0) break
                totalCargoWeight += nextProduct.weight
                cargo.add(nextProduct)
        }
    }
    cargo.sortBy { it.loadingTime }
    cargo.reverse()
    return cargo
}