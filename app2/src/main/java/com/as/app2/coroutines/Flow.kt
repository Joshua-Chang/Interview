package com.`as`.app2.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

suspend fun simpleList(): List<Int> {
    delay(1000L)
    return listOf(1, 2, 3)
}

fun simpleSequence(): Sequence<Int> = sequence {//序列构建器
    for (i in 1..3) {
        Thread.sleep(100)
        yield(i)
    }
}

/**
 * 一.
 * 名为 flow 的 Flow 类型构建器函数。
 * flow { ... } 构建块中的代码可以挂起。
 * 函数 simple 不再标有 suspend 修饰符。
 * 流使用 emit 函数 发射 值。
 * 流使用 collect 函数 收集 值。
 */
fun simpleFLow(): Flow<Int> = flow { // 流构建器
    println("Flow started")
    for (i in 1..3) {
        delay(100L)
        emit(i)
    }
}


fun main(args: Array<String>) {
//    simpleSequence().forEach { value -> println(value) }

//    runBlocking {
//        simpleList().forEach { value -> println(value) }
//    }

    //二.流构建器除了最基础的flow{...} 还有
    //flowOf处理固定值/集合
    //asFlow将集合/序列转化为流
    val flowOf = flowOf(1, 2, 3)
    val asFlow = listOf(1, 2, 3).asFlow()
    runBlocking {
        flowOf.collect { value -> println(value) }
        asFlow.collect { value -> println(value) }
    }

    runBlocking {
        println("calling fun")
        var flow =simpleFLow()
        println("calling collect")
        flow.collect { value -> println(value) }// 1.冷流:flow 构建器中的代码直到流被收集的时候才运行
        println("calling collect again")
        flow.collect { value -> println(value) }

        withTimeoutOrNull(200L){//2.超时的情况下取消并停止执行其代码
            flow.collect { value -> println(value) }
        }

    }
}
