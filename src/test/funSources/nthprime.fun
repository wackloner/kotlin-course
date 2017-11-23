// checks if number is prime
fun isPrime(n) {
    println(n)
    var div = 2
    while (div * div <= n) {
        println(div, div)
        if (n % div == 0) {
            println(-1, -1)
            return 0
        }
        println(1448)
        div = div + 1
    }
    println(-1)
    return 1
}

// finds nth prime number
fun getNthPrime(n) {
    var cnt = 0
    var i = 2
    var lastPrime = -1
    while (cnt < n) {
        println(i, i, i)
        if (isPrime(i)) {
            println(228)
            lastPrime = i
            cnt = cnt + 1
        }
        println(i)
        i = i + 1
        println(i)
    }
    return lastPrime
}

//var n = 1
//while (n <= 5) {
//    println(n, getNthPrime(n))
//    n = n + 1
//}

println(getNthPrime(3))
