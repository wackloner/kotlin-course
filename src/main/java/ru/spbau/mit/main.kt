package ru.spbau.mit

import java.util.*

data class Request(
        val p: Int,
        val c: Char
)

class Solver {
    class SegmentTree(
            private val size: Int
    ) {
        private val tree = Array(size * 4, { 0 })

        private fun getPositionOf(v: Int, l: Int, r: Int, k: Int): Int? {
            if (l == r)
                return if (tree[v] == 0) null else l
            val m = (l + r) / 2
            return when (tree[v * 2 + 1] >= k) {
                true -> getPositionOf(v * 2 + 1, l, m, k)
                false -> getPositionOf(v * 2 + 2, m + 1, r, k - tree[v * 2 + 1])
            }
        }

        fun getPositionOf(k: Int) = getPositionOf(0, 0, size - 1, k)

        private fun add(v: Int, l: Int, r: Int, position: Int, delta: Int) {
            if (l == r)
                tree[v] += delta
            else {
                val m = (l + r) / 2
                if (position <= m) {
                    add(v * 2 + 1, l, m, position, delta)
                } else {
                    add(v * 2 + 2, m + 1, r, position, delta)
                }
                tree[v] = tree[v * 2 + 1] + tree[v * 2 + 2]
            }
        }

        fun add(position: Int, delta: Int) = add(0, 0, size - 1, position, delta)
    }

    fun solve(k: Int, s: String, requests: Array<Request>): String {
        val length = k * s.length
        val trees = Array(26, { SegmentTree(length) })
        for (i in 0 until k)
            for (j in 0 until s.length) {
                val index = s[j] - 'a'
                val position = i * s.length + j
                trees[index].add(position, 1)
            }
        for ((p, c) in requests) {
            val index = c - 'a'
            val position = trees[index].getPositionOf(p)
            trees[index].add(position!!, -1)
        }
        val totalString = Array(length, { ' ' })
        for (letter in 'a'..'z') {
            val index = letter - 'a'
            while (true) {
                val position = trees[index].getPositionOf(1)
                position ?: break
                totalString[position] = letter
                trees[index].add(position, -1)
            }
        }
        return totalString.filter { c -> c != ' ' }.toCharArray().joinToString("")
    }
}

fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    val k = input.nextInt()
    val s = input.next()
    val n = input.nextInt()
    val requests = Array(n,
            { Request(input.nextInt(), input.next().single()) })
    println(Solver().solve(k, s, requests))
}
