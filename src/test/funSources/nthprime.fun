// checks if number is prime
fun isPrime(n) {
    var div = 2
    while (div * div <= n) {
        if (n % div == 0) {
            return 0
        }
        div = div + 1
    }
    return 1
}

// finds nth prime number
fun getNthPrime(n) {
    var cnt = 0
    var i = 2
    var lastPrime = -1
    while (cnt < n) {
        if (isPrime(i)) {
            lastPrime = i
            cnt = cnt + 1
        }
        i = i + 1
    }
    return lastPrime
}

var n = 1
while (n <= 5) {
    println(n, getNthPrime(n))
    n = n + 1
}
