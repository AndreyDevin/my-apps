data class Product(
    val name: String,
    val food: Boolean,
    val breakingCargo: Boolean,
    val weight: Int,
    val loadingTime: Long
)

object ProductList {
    val productList = listOf(
        Product(name = "Potatoes", food = true, breakingCargo = false, weight = 600, loadingTime = 150),
        Product(name = "Carrots", food = true, breakingCargo = false, weight = 400, loadingTime = 100),
        Product(name = "Apples", food = true, breakingCargo = false, weight = 200, loadingTime = 50),
        Product(name = "Oranges", food = true, breakingCargo = false, weight = 100, loadingTime = 20),

        Product(name = "Beer", food = true, breakingCargo = true, weight = 1000, loadingTime = 100),
        Product(name = "Vodka", food = true, breakingCargo = true, weight = 333, loadingTime = 50),
        Product(name = "Tequila", food = true, breakingCargo = true, weight = 150, loadingTime = 30),
        Product(name = "Whisky", food = true, breakingCargo = true, weight = 150, loadingTime = 30),

        Product(name = "TV", food = false, breakingCargo = true, weight = 500, loadingTime = 120),
        Product(name = "Phone", food = false, breakingCargo = true, weight = 50, loadingTime = 10),
        Product(name = "Laptop", food = false, breakingCargo = true, weight = 200, loadingTime = 40),
        Product(name = "Fridge", food = false, breakingCargo = true, weight = 1500, loadingTime = 350),

        Product(name = "Brick", food = false, breakingCargo = false, weight = 5000, loadingTime = 250),
        Product(name = "Planks", food = false, breakingCargo = false, weight = 2000, loadingTime = 300),
        Product(name = "Clothes", food = false, breakingCargo = false, weight = 100, loadingTime = 10),
        Product(name = "Shoes", food = false, breakingCargo = false, weight = 200, loadingTime = 15)
    )
}