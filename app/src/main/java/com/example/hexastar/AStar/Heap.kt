package com.example.hexastar.AStar

import android.util.Log
import com.example.hexastar.A_STAR_DEBUG

class Heap<T: Comparable<T>>(private val maxSize: Int, private val mode: Int = MIN_MODE) {
    private val array = ArrayList<T?>()
    private var curHeapSize: Int = 0

    init {
        repeat(maxSize) {
            array.add(null)
        }
    }
    fun get() = if (array.isNotEmpty()) array[0] else error("Heap is empty")
    fun add(value: T) {
        if (isFull()) {
            error("Heap is full")
        }
        var index = curHeapSize

        array[curHeapSize++] = value

        if (curHeapSize == 1) return
        if (isFull()) {
            error("Heap is full");
        }
        while (index > 0 &&
            !(mode == MIN_MODE && array[(index - 1) / 2]!! < array[index]!!) &&
            !(mode == MAX_MODE && array[(index - 1) / 2]!! > array[index]!!)
        ) {
            swap(index, (index - 1) / 2)
            index = (index - 1) / 2;
        }
    }
    private fun swap(index1: Int, index2: Int) {
        val temp = array[index1]!!
        array[index1] = array[index2]
        array[index2] = temp
    }
    fun pop() {
        if (isEmpty()) {
            error("Heap is empty");
        }
        if (curHeapSize == 1) {
            curHeapSize = 0
            return
        }
        array[0] = array[--curHeapSize]

        var index = 0
        var left: Int
        var right: Int
        var minOrMaxIndex: Int

        while (true) {
            left = 2 * index + 1
            right = 2 * index + 2
            minOrMaxIndex = index
            if (left < curHeapSize && (
                (mode == MIN_MODE && array[left]!! < array[minOrMaxIndex]!!)
                    ||
                (mode == MAX_MODE && array[left]!! > array[minOrMaxIndex]!!)
            )) {
                minOrMaxIndex = left
            }
            if (right < curHeapSize && (
                (mode == MIN_MODE && array[right]!! < array[minOrMaxIndex]!!)
                    ||
                (mode == MAX_MODE && array[right]!! > array[minOrMaxIndex]!!)
            )) {
                minOrMaxIndex = right
            }
            if (minOrMaxIndex != index) {
                swap(index, minOrMaxIndex)
                index = minOrMaxIndex
            }
            else {
                break
            }
        }
    }
    fun getAndPop(): T? {
        val min = get()
        pop()
        return min
    }
    fun isEmpty() = curHeapSize == 0
    fun isFull() = curHeapSize == maxSize
    fun getSize() = curHeapSize
    override fun toString(): String {
        var heapString = ""
        var step = 1
        var dStep = 2
        for (i in 0..<curHeapSize) {
            heapString += "${array[i]} "
            --step
            if (step == 0) {
                step += dStep
                dStep *= 2
                heapString += "\n"
            }
        }
        heapString += "\n"
        return heapString
    }
    fun printHeap() {
        var step = 1
        var dStep = 2
        for (i in 0 until curHeapSize) {
            Log.d(A_STAR_DEBUG, array[i].toString())
            --step
            if (step == 0) {
                step += dStep
                dStep *= 2
            }
        }
    }
    fun printArray() {
        for (i in 0 until curHeapSize) {
            Log.d(A_STAR_DEBUG, array[i].toString())
        }
    }
    companion object {
        fun<T : Comparable<T>> heapSort(array: ArrayList<T>) {
            val heap = Heap<T>(array.size)
            array.forEach {
                heap.add(it)
            }
            for (i in array.size - 1 downTo 0) {
                array[i] = heap.getAndPop()!!
            }
        }
        const val MAX_MODE = 0
        const val MIN_MODE = 1
    }
}
